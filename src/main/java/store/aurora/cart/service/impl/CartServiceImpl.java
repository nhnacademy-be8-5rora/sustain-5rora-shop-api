package store.aurora.cart.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.aurora.book.dto.BookInfoDTO;
import store.aurora.book.entity.Book;
import store.aurora.book.service.BookService;
import store.aurora.cart.dto.CartDTO;
import store.aurora.cart.dto.CartItemDTO;
import store.aurora.cart.entity.Cart;
import store.aurora.cart.entity.CartItem;
import store.aurora.cart.repository.CartItemRepository;
import store.aurora.cart.repository.CartRepository;
import store.aurora.cart.service.CartService;
import store.aurora.user.service.UserService;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

@Slf4j
@Service
public class CartServiceImpl implements CartService {
    private static final String CART_COOKIE_NAME = "CART";

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    private final UserService userService;
    private final BookService bookService;

    public CartServiceImpl(CartRepository cartRepository, CartItemRepository cartItemRepository, UserService userService, BookService bookService) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userService = userService;
        this.bookService = bookService;
    }

    private Cart getUserCart(String userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> createUserCart(userId));
    }

    @Override
    @Transactional
    public Map<String, Object> getUserCartWithTotalPrice(String userId) {
        Cart cart = getUserCart(userId);

        List<CartDTO> cartItems = cartItemRepository.findByCartId(cart.getId()).stream()
                .map(cartItem -> new CartDTO(
                        cartItem.getBook().getId(),
                        cartItem.getQuantity()
                )).toList();

        return getBookInfo(cartItems);
    }

    @Override
    @Transactional
    public void addItemToCart(String userId, Long bookId, int quantity) {
        Cart cart = getUserCart(userId);

        Optional<CartItem> cartItemOptional = cartItemRepository.findByCartAndBookId(cart, bookId);
        if(cartItemOptional.isEmpty()) {
            createCartItem(cart, bookId, quantity);
        }else {
            CartItem cartItem = cartItemOptional.get();
            cartItem.setQuantity(quantity);
        }
    }

    @Override
    @Transactional
    public void deleteCartItem(String userId, Long bookId) {
        Cart cart = getUserCart(userId);
        cart.getCartItems().removeIf(cartItem -> Objects.equals(cartItem.getId(), bookId)); // todo 현재는 존재하지 않는 북 삭제시 그냥 넘어감
    }

    @Override
    public Cart createUserCart(String userId) {
        Cart cart = new Cart(userService.getUser(userId));
        return cartRepository.save(cart);
    }

    private void createCartItem(Cart cart, Long bookId, int quantity) {
        Book book = bookService.getBookById(bookId);
        CartItem cartItem = new CartItem(cart, book);
        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
    }

    //////////////////////// 비인증자 //////////////////////////////

    @Override
    public void addItemToGuestCart(Long bookId, int quantity, HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException, JsonProcessingException {
        List<CartItemDTO> cartItems = getCartFromCookie(request);

        // 기존 장바구니에서 해당 상품이 있는지 확인
        Optional<CartItemDTO> existingItem = cartItems.stream()
                .filter(item -> item.getBookId().equals(bookId))
                .findFirst();

        if (existingItem.isPresent()) {
            // 이미 장바구니에 있으면 수량만 업데이트
            CartItemDTO item = existingItem.get();
            item.setQuantity(quantity);
        } else {
            // 장바구니에 없으면 새로 추가
//            bookService.notExistThrow(bookId); // todo
            cartItems.add(new CartItemDTO(bookId, quantity));
        }

        saveCookie(cartItems, response);
    }

    @Override
    public void deleteGuestCartItem(Long bookId, HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException, JsonProcessingException {
        List<CartItemDTO> cartItems = getCartFromCookie(request);
        cartItems.removeIf(item -> item.getBookId().equals(bookId));
        saveCookie(cartItems, response);
    }

    @Override
    @Transactional
    public Map<String, Object> getGuestCartWithTotalPrice(HttpServletRequest request) {
        List<CartItemDTO> cartItemResponseDTOS = getCartFromCookie(request);

        List<CartDTO> cartItems = cartItemResponseDTOS.stream()
                .map(cartItem -> new CartDTO(
                        cartItem.getBookId(),
                        cartItem.getQuantity()
                )).toList();

        return getBookInfo(cartItems);
    }

    private Map<String, Object> getBookInfo(List<CartDTO> cartItems) {
        List<Long> bookIds = cartItems.stream()
                .map(CartDTO::getBookId)
                .toList();

        List<BookInfoDTO> bookInfoList = bookService.getBookInfo(bookIds);
        for (int i = 0; i < cartItems.size(); i++) {
            CartDTO cartItem = cartItems.get(i);
            BookInfoDTO bookInfo = bookInfoList.get(i);
            cartItem.setTitle(bookInfo.getTitle());
            cartItem.setRegularPrice(bookInfo.getRegularPrice());
            cartItem.setSalePrice(bookInfo.getSalePrice());
            cartItem.setStock(bookInfo.getStock());
            cartItem.setSale(bookInfo.isSale());
            cartItem.setFilePath(bookInfo.getFilePath());
        }

        int totalPrice = cartItems.stream()
                .mapToInt(cartItem -> cartItem.getSalePrice() * cartItem.getQuantity())
                .sum();

        Map<String, Object> result = new HashMap<>();
        result.put("cartItems", cartItems);
        result.put("totalPrice", totalPrice);
        // todo : result.put("point", )
        return result;
    }

    private List<CartItemDTO> getCartFromCookie(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (CART_COOKIE_NAME.equals(cookie.getName())) {
                    try {
                        // URL 디코딩
                        String decodedCartJson = URLDecoder.decode(cookie.getValue(), "UTF-8");

                        // JSON 파싱 (ObjectMapper 사용 예시)
                        return objectMapper.readValue(decodedCartJson, new TypeReference<List<CartItemDTO>>(){});
//                        return objectMapper.readValue(cookie.getValue(), objectMapper.getTypeFactory().constructCollectionType(List.class, CartItemResponseDTO.class));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return new ArrayList<>(); // 장바구니 쿠키가 없으면 빈 리스트 반환
    }

    private void saveCookie(List<CartItemDTO> cartItems, HttpServletResponse response) throws UnsupportedEncodingException, JsonProcessingException {
        String cartJson = objectMapper.writeValueAsString(cartItems);
        log.debug("cart cookie to send: {}", cartJson);
        String encodedCartJson = URLEncoder.encode(cartJson, "UTF-8");
        Cookie cartCookie = new Cookie(CART_COOKIE_NAME, encodedCartJson);
        cartCookie.setMaxAge(60 * 60 * 24); // 24시간
        cartCookie.setHttpOnly(true);
        cartCookie.setPath("/"); // 모든 경로에서 접근 가능
//        cartCookie.setSecure(true); // todo
        response.addCookie(cartCookie);
    }
}
