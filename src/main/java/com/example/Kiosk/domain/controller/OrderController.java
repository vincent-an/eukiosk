package com.example.Kiosk.domain.controller;

import com.example.Kiosk.domain.dto.request.CartItemAddRequest;
import com.example.Kiosk.domain.dto.request.CartItemUpdateRequest;
import com.example.Kiosk.domain.dto.request.OrderCreateRequest;
import com.example.Kiosk.domain.dto.response.CartResponse;
import com.example.Kiosk.domain.dto.response.CompletedOrderResponse;
import com.example.Kiosk.domain.dto.response.OrderStartResponse;
import com.example.Kiosk.domain.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;


    // 주문 시작
    @PostMapping
    public ResponseEntity<OrderStartResponse> createOrder(
            @RequestBody @Valid OrderCreateRequest request
    ) {

        OrderStartResponse response = orderService.createNewOrder(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 장바구니(Orders)에 메뉴 항목(OrderItem) 추가
    @PostMapping("/{orderId}/items")
    public ResponseEntity<CartResponse> addItemToCart(@PathVariable Long orderId,
                                                      @RequestBody @Valid CartItemAddRequest request) {
        log.info("장바구니 항목 추가 요청 - Order ID: {}, Menu ID: {}", orderId, request.getMenuId());

        CartResponse cartResponse = orderService.addItemToOrder(orderId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(cartResponse);
    }

    // 장바구니 조회
    @GetMapping("/{orderId}")
    public ResponseEntity<CartResponse> getCart(@PathVariable Long orderId) {
        CartResponse cartResponse = orderService.getCart(orderId);
        log.info("장바구니 조회 완료 (Order ID: {})", orderId);
        return ResponseEntity.ok(cartResponse);
    }

    // 장바구니 항목 수량 변경
    @PatchMapping("/{orderId}/items/{orderItemId}")
    public ResponseEntity<CartResponse> updateCartItem(
            @PathVariable Long orderId,
            @PathVariable Long orderItemId,
            @RequestBody @Valid CartItemUpdateRequest request
    ) {
        CartResponse cartResponse = orderService.updateItemQuantity(orderId, orderItemId, request);

        return ResponseEntity.ok(cartResponse);
    }

    // 장바구니 항목 삭제
    @DeleteMapping("/{orderId}/items/{orderItemId}")
    public ResponseEntity<CartResponse> deleteCartItem(
            @PathVariable Long orderId,
            @PathVariable Long orderItemId) {
        CartResponse cartResponse = orderService.deleteCartItem(orderId, orderItemId);
        return ResponseEntity.ok(cartResponse);
    }


    // 주문 완료 버튼 눌렀을 때 기능
    @PostMapping("/{orderId}/complete")
    public ResponseEntity<CompletedOrderResponse> completeOrder(
            @PathVariable Long orderId) {
        // 1. 서비스 호출
        CompletedOrderResponse response = orderService.completeOrder(orderId);

        // 2. 200 OK 상태와 완료된 주문 DTO를 반환
        return ResponseEntity.ok(response);
    }

}
