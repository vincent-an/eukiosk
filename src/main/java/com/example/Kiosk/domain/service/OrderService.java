package com.example.Kiosk.domain.service;

import com.example.Kiosk.domain.dto.request.CartItemAddRequest;
import com.example.Kiosk.domain.dto.request.CartItemUpdateRequest;
import com.example.Kiosk.domain.dto.request.OptionAddRequest;
import com.example.Kiosk.domain.dto.request.OrderCreateRequest;
import com.example.Kiosk.domain.dto.response.CartResponse;
import com.example.Kiosk.domain.dto.response.CompletedOrderResponse;
import com.example.Kiosk.domain.dto.response.OrderStartResponse;
import com.example.Kiosk.domain.entity.*;
import com.example.Kiosk.domain.entity.eums.OrderStatus;
import com.example.Kiosk.domain.repository.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrdersRepository ordersRepository;
    private final MenuRepository menuRepository;
    private final OptionRepository optionRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderItemOptionRepository orderItemOptionRepository;

    private final AtomicInteger orderNumberGenerator = new AtomicInteger(100);

    public CartResponse addItemToOrder(Long orderId, @Valid CartItemAddRequest request) {
        log.info("장바구니(ID:{}) 항목 추가 시작", orderId);

        // Order 찾기
        Orders orders = ordersRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("장바구니를 찾을 수 없습니다. : " + orderId));

        // 메뉴 찾기(기본 가격 확인)
        Menu menu = menuRepository.findById(request.getMenuId())
                .orElseThrow(() -> new IllegalArgumentException("메뉴를 찾을 수 없습니다. : " + request.getMenuId()));

        // 옵션 가격 확인, 요청 DTO에서 Option Id 리스트를 출력해아 함
        List<Long> optionIds = request.getOptions().stream()
                .map(OptionAddRequest::getOptionId)
                .toList();

        // 위에서 받은 Id 리스트로 Option 엔티티 리스트를 DB에서 조회
        List<Option> optionList = optionRepository.findAllById(optionIds);

        // 조회된 리스트를 Map형식으로 변환함 why? 빠른 가격 검색을 위해
        Map<Long, Option> optionMap = optionList.stream()
                .collect(Collectors.toMap(Option::getOptionId, option -> option));

        // -- 가격 계산 시작 --
        log.info("가격 계산 시작...");

        // a. (옵션 총합) = (옵션1 가격 * 옵션1 수량) + (옵션2 가격 * 옵션2 수량) ...
        int optionsTotalPrice = 0;
        for (OptionAddRequest optionReq : request.getOptions()) {
            Option option = optionMap.get(optionReq.getOptionId());
            optionsTotalPrice += (option.getPrice() * optionReq.getQuantity());
        }
        log.info("옵션 총합 가격: {}", optionsTotalPrice);

        // b. (항목 총합) = (메뉴 기본가 + 옵션 총합) * 전체 수량
        int itemTotalPrice = (menu.getPrice() + optionsTotalPrice) * request.getQuantity();
        log.info("항목 총합 가격 = {}", itemTotalPrice);

        // --- 새 엔티티 생성 및 관계 설정 (DB에 저장할 객체 조립) ---
        log.info("새 OrderItem 및 OrderItemOption 엔티티 생성...");

        // a. 새 주문 항목(orderItem) 엔티티 생성
        OrderItem orderItem = new OrderItem();
        orderItem.setMenu(menu);
        orderItem.setQuantity(request.getQuantity());
        orderItem.setItemTotalPrice(itemTotalPrice);
        orderItem.setOrders(orders); // 부모 (Orders)과 연결

        // b. '주문 항목 옵션(OrderItemOption)' 엔티티 생성
        for (OptionAddRequest optionReq : request.getOptions()) {
            Option option = optionMap.get(optionReq.getOptionId());

            OrderItemOption orderItemOption = new OrderItemOption();
            orderItemOption.setOption(option);
            orderItemOption.setQuantity(optionReq.getQuantity());
            orderItemOption.setOrderItem(orderItem); // 부모(OrderItem)와 연결

            // OrderItem의 자식 리스트에 추가 (JPA Cascade를 위함)
            orderItem.getOrderItemOptions().add(orderItemOption);
        }
        // Orders의 자식 리스트에 추가 (JPA Cascade를 위함)
        orders.getOrderItems().add(orderItem);

        // --- 4. 장바구니 총액 갱신 ---
        log.info("장바구니 총액 갱신...");

//         (Null 방지) 기존 장바구니 총액이 null이면 0으로 시작
        int currentCartTotal = (orders.getTotalPrice() == null) ? 0 : orders.getTotalPrice();
        orders.setTotalPrice(currentCartTotal + itemTotalPrice);

        // --- 5. DB 저장 ---
        // (중요!) @Transactional + Cascade 설정 덕분에,
        // 부모인 'Orders'만 저장해도 자식인 'OrderItem'과
        // 손자인 'OrderItemOption'까지 모두 DB에 INSERT 됩니다.
        Orders savedOrders = ordersRepository.save(orders);
        log.info("장바구니(ID:{}) 저장 완료. 새 총액: {}", savedOrders.getOrderId(), savedOrders.getTotalPrice());


        // --- 6. 갱신된 DTO 반환 ---
        // 컨트롤러에게 갱신된 장바구니(Orders) 정보를 DTO로 변환하여 반환
        return CartResponse.from(savedOrders);
    }


    // 장바구니 목록 조회
    @Transactional(readOnly = true)
    public CartResponse getCart(Long orderId) {
        log.info("장바구니 조회 요청 (Order ID: {})", orderId);

        // 조회 id 찾기
        Orders orders = ordersRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("장바구니를 찾을 수 없습니다. : " + orderId));

        return CartResponse.from(orders);
    }

    // 장바구니 목록에서 메뉴 수량 변경
    public CartResponse updateItemQuantity(Long orderId, Long orderItemId, CartItemUpdateRequest request) {
        log.info("장바구니 항목 수량 변경 요청 (Order ID: {}, Item ID: {}) -> 새 수량: {}",
                orderId, orderItemId, request.getQuantity());

        // 장바구니 및 개별 항목 조회
        Orders orders = ordersRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("장바구니를 찾을 수 없습니다. : " + orderId));

        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new IllegalArgumentException("해당 주문 메뉴를 찾을 수 없습니다. : " + orderItemId));

        // 장바구니와 개별 항목 동일 검증
        if (!orderItem.getOrders().getOrderId().equals(orderId)) {
            throw new IllegalArgumentException("장바구니와 항목이 일치하지 않습니다.");
        }

        // 음료 단가 계산 및 항목 총 금액
        int unitPrice = orderItem.getItemTotalPrice() / orderItem.getQuantity();
        int newItemTotalPrice = request.getQuantity() * unitPrice;
        // 엔티티 업데이트
        orderItem.setQuantity(request.getQuantity());
        orderItem.setItemTotalPrice(newItemTotalPrice);

        updateCartTotalPrice(orders);

        Orders savedOrders = ordersRepository.save(orders);
        log.info("항목(ID:{}) 수량 변경 완료. 새 항목 총액: {}", orderItemId, newItemTotalPrice);

        return CartResponse.from(savedOrders);
    }

    // 장바구니 변경 계산 메서드
    private void updateCartTotalPrice(Orders orders) {
        log.info("장바구니(ID:{}) 총액 재계산", orders.getOrderId());

        int newTotalPrice = orders.getOrderItems().stream()
                .mapToInt(OrderItem::getItemTotalPrice)
                .sum();

        orders.setTotalPrice(newTotalPrice);
        log.info("새 총액: {}", newTotalPrice);
    }

    //장바구니 항목 삭제 기능
    public CartResponse deleteCartItem(Long orderId, Long orderItemId) {
        log.info("장바구니 항목 삭제 요청 (Order ID: {}, Item ID: {})", orderId, orderItemId);

        // 1. 엔티티 조회
        Orders orders = ordersRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("장바구니를 찾을 수 없습니다: " + orderId));

        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new IllegalArgumentException("항목을 찾을 수 없습니다: " + orderItemId));

        // 2. (보안 검증) 소속 확인
        if (!orderItem.getOrders().getOrderId().equals(orderId)) {
            throw new IllegalArgumentException("장바구니와 항목이 일치하지 않습니다.");
        }

        //삭제 시작
        orders.getOrderItems().remove(orderItem);
        orderItemRepository.delete(orderItem);
        updateCartTotalPrice(orders);
        Orders savedOrders = ordersRepository.save(orders);

        log.info("항목(ID:{}) 삭제 완료. 새 총액: {}", orderItem.getOrderItemId(), savedOrders.getTotalPrice());

        return CartResponse.from(savedOrders);
    }


    public CompletedOrderResponse completeOrder(Long orderId) {
        log.info("주문 완료 처리 요청 (Order ID: {})", orderId);

        Orders orders = ordersRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("장바구니를 찾을 수 없습니다: " + orderId));

        if (orders.getStatus() == OrderStatus.COMPLETED) {
            log.warn("이미 완료된 주문(ID:{})입니다.", orderId);
            // 완료 된 경우, 그대로 반환
            return CompletedOrderResponse.from(orders);
        }

        // 3. 주문번호 생성
        // (100 -> 101, 101 -> 102)
        int newOrderNumber = orderNumberGenerator.incrementAndGet();

        orders.setStatus(OrderStatus.COMPLETED); // (상태: 완료)
        orders.setOrderNumber(newOrderNumber); // (주문번호 저장)
        orders.setOrderTime(LocalDateTime.now()); // (주문 완료 시간)

        Orders completedOrders = ordersRepository.save(orders);

        log.info("주문(ID:{}) 완료. 주문번호: {}", orderId, newOrderNumber);

        return CompletedOrderResponse.from(completedOrders);
    }

    // 주문 생성
    public OrderStartResponse createNewOrder(OrderCreateRequest request) {
        log.info("새 주문 시작 요청 (타입: {})", request.getOrderType());

        Orders newOrder = new Orders();

        // 2. 초기 상태 및 값 설정
        newOrder.setStatus(OrderStatus.PENDING); // (상태: 주문 중)
        newOrder.setTotalPrice(0); // (초기 총액: 0)

        newOrder.setOrderType(request.getOrderType());

        Orders savedOrder = ordersRepository.save(newOrder);

        log.info("새 장바구니(Order ID:{}) 생성 완료", savedOrder.getOrderId());

        return OrderStartResponse.from(savedOrder);
    }
}
