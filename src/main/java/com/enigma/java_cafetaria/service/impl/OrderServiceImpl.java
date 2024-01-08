package com.enigma.java_cafetaria.service.impl;


import com.enigma.java_cafetaria.constant.EOrderType;
import com.enigma.java_cafetaria.dto.requets.OrderRequest;
import com.enigma.java_cafetaria.dto.response.CategoryResponse;
import com.enigma.java_cafetaria.dto.response.MenuResponse;
import com.enigma.java_cafetaria.dto.response.OrderDetailResponse;
import com.enigma.java_cafetaria.dto.response.OrderResponse;
import com.enigma.java_cafetaria.entity.MenuPrice;
import com.enigma.java_cafetaria.entity.Order;
import com.enigma.java_cafetaria.entity.OrderDetail;
import com.enigma.java_cafetaria.repository.*;
import com.enigma.java_cafetaria.service.CategoryService;
import com.enigma.java_cafetaria.service.MenuPriceService;
import com.enigma.java_cafetaria.service.MenuService;
import com.enigma.java_cafetaria.service.OrderService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Transactional(rollbackOn = Exception.class)
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    @PersistenceContext
    private EntityManager entityManager;

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final MenuPriceRepository menuPriceRepository;
    private final MenuRepository menuRepository;
    private final MenuService menuService;
    private final MenuPriceService menuPriceService;
    private final CategoryRepository categoryRepository;
    private final CategoryService categoryService;

    public static String generateReceiptNumber(String branchCode, int year, int sequenceNumber) {
        return String.format("%s-%d-%d", branchCode, year, sequenceNumber);
    }


    @Override
    public OrderResponse createNewOrder(OrderRequest orderRequest) {
        // Todo 1: convert orderDetailRequest to orderDetail
        List<OrderDetail> orderDetails = orderRequest.getBillDetails().stream()
                .map(orderDetailRequest -> {
                    // Todo 3: validasi product price
                    MenuPrice menuPrice = menuPriceService.getById(orderDetailRequest.getMenuPriceId());
                    return OrderDetail.builder()
                            .menuPrice(menuPrice)
                            .quantity(orderDetailRequest.getQuantity())
                            .build();
                })
                .toList();

        String categoryCode = "CC";
        int currentYear = LocalDateTime.now().getYear();
        int sequenceNumber = 1;

        // Menggunakan nilai transactionType langsung dari TransactionRequest transaction type
        EOrderType orderType = orderRequest.getOrderType();

        String receiptNumber = generateReceiptNumber(categoryCode, currentYear, sequenceNumber);

        // Todo 4: Create New transaction
        String nativeInsertOrderQuery = "INSERT INTO t_order (id, receipt_number, trans_date, order_type) VALUES (?, ?, ?, ?)";
        UUID orderId = UUID.randomUUID(); // Menghasilkan UUID baru
        Query insertOrderQuery = entityManager.createNativeQuery(nativeInsertOrderQuery);
        insertOrderQuery.setParameter(1, orderId);
        insertOrderQuery.setParameter(2, receiptNumber);
        insertOrderQuery.setParameter(3, Timestamp.valueOf(LocalDateTime.now()));
        insertOrderQuery.setParameter(4, (short) orderType.ordinal()); // Konversi ke tipe data short
        insertOrderQuery.executeUpdate();

        // Todo 5: Get the inserted order
        Order savedOrder = orderRepository.findById(orderId.toString()).orElse(null);

        // Todo 6: Set order for orderDetails and save orderDetails using native query
        for (OrderDetail orderDetail : orderDetails) {
            orderDetail.setOrder(savedOrder);
            entityManager.persist(orderDetail);
        }

        // Todo 7: Get the saved orderDetails
        List<OrderDetail> savedOrderDetails = orderDetailRepository.findByOrderId(savedOrder.getId());

        List<OrderDetailResponse> orderDetailResponses = savedOrderDetails.stream()
                .map(orderDetail -> OrderDetailResponse.builder()
                        .orderDetailId(orderDetail.getId())
                        .quantity(orderDetail.getQuantity())
                        .menuResponse(MenuResponse.builder()
                                .menuId(orderDetail.getMenuPrice().getMenu().getId())
                                .menuPriceId(orderDetail.getMenuPrice().getId())
                                .menuCode(orderDetail.getMenuPrice().getMenu().getMenuCode())
                                .menuName(orderDetail.getMenuPrice().getMenu().getMenuName())
                                .status(orderDetail.getMenuPrice().getMenu().getStatus())
                                .price(orderDetail.getMenuPrice().getPrice())
                                .category(CategoryResponse.builder()
                                        .id(orderDetail.getMenuPrice().getCategory().getId())
                                        .categoryCode(orderDetail.getMenuPrice().getCategory().getCategoryCode())
                                        .categoryName(orderDetail.getMenuPrice().getCategory().getCategoryName())
                                        .build())
                                .build())
                        .build())
                .toList();

        // Todo 8: Return orderResponse
        return OrderResponse.builder()
                .billId(savedOrder.getId().toString()) // Menggunakan ID sebagai String
                .receiptNumber(savedOrder.getReceiptNumber())
                .transDate(savedOrder.getTransDate())
                .orderType(savedOrder.getOrderType())
                .orderDetailResponses(orderDetailResponses)
                .build();
    }


    @Override
    public OrderResponse getOrderById(String id) {
        String nativeQuery = "SELECT " +
                "o.id AS billId, " +
                "o.receipt_number AS receiptNumber, " +
                "o.trans_date AS transDate, " +
                "o.order_type AS orderType, " +
                "od.id AS orderDetailId, " +
                "mp.id AS menuPriceId, " +
                "m.id AS menuId, " +
                "m.menu_code AS menuCode, " +
                "m.menu_name AS menuName, " +
                "m.status AS status, " +
                "mp.price AS price, " +
                "c.id AS categoryId, " +
                "c.category_code AS categoryCode, " +
                "c.category_name AS categoryName, " +
                "od.quantity " +
                "FROM t_order o " +
                "JOIN t_order_detail od ON o.id = od.order_id " +
                "JOIN m_menu_price mp ON od.menu_price_id = mp.id " +
                "JOIN m_menu m ON mp.menu_id = m.id " +
                "JOIN m_category c ON m.category_id = c.id " +
                "WHERE o.id = ?"; // Parameterized query to prevent SQL injection

        Query query = entityManager.createNativeQuery(nativeQuery);
        query.setParameter(1, id);

        try {
            Object[] result = (Object[]) query.getSingleResult();

            String billId = (String) result[0];

            List<OrderDetailResponse> orderDetailResponses = new ArrayList<>();

            MenuResponse menuResponse = MenuResponse.builder()
                    .menuId((String) result[6])
                    .menuPriceId((String) result[5])
                    .menuCode((String) result[7])
                    .menuName((String) result[8])
                    .status((String) result[9])
                    .price((Long) result[10])
                    .category(CategoryResponse.builder()
                            .id((String) result[11])
                            .categoryCode((String) result[12])
                            .categoryName((String) result[13])
                            .build())
                    .build();

            OrderDetailResponse orderDetailResponse = OrderDetailResponse.builder()
                    .orderDetailId((String) result[4])
                    .quantity((Integer) result[14])
                    .menuResponse(menuResponse)
                    .build();

            orderDetailResponses.add(orderDetailResponse);

            return OrderResponse.builder()
                    .billId(billId)
                    .receiptNumber((String) result[1])
                    .transDate(((Timestamp) result[2]).toLocalDateTime())
                    .orderType(EOrderType.fromString(result[3].toString()).orElse(null))
                    .orderDetailResponses(orderDetailResponses)
                    .build();
        } catch (NoResultException e) {
            return null; // Order not found
        }
    }

    @Override
    public List<OrderResponse> getAllOrder() {
        String nativeQuery = "SELECT " +
                "o.id AS billId, " +
                "o.receipt_number AS receiptNumber, " +
                "o.trans_date AS transDate, " +
                "o.order_type AS orderType, " +
                "od.id AS orderDetailId, " +
                "mp.id AS menuPriceId, " +
                "m.id AS menuId, " +
                "m.menu_code AS menuCode, " +
                "m.menu_name AS menuName, " +
                "m.status AS status, " +
                "mp.price AS price, " +
                "c.id AS categoryId, " +
                "c.category_code AS categoryCode, " +
                "c.category_name AS categoryName, " +
                "od.quantity " +
                "FROM t_order o " +
                "JOIN t_order_detail od ON o.id = od.order_id " +
                "JOIN m_menu_price mp ON od.menu_price_id = mp.id " +
                "JOIN m_menu m ON mp.menu_id = m.id " +
                "JOIN m_category c ON m.category_id = c.id";


        Query query = entityManager.createNativeQuery(nativeQuery);

        List<Object[]> results = query.getResultList();

        Map<String, OrderResponse> orderResponseMap = new HashMap<>();

        for (Object[] result : results) {
            String billId = (String) result[0];

            OrderResponse orderResponse = orderResponseMap.get(billId);

            if (orderResponse == null) {
                orderResponse = OrderResponse.builder()
                        .billId(billId)
                        .receiptNumber((String) result[1])
                        .transDate(((Timestamp) result[2]).toLocalDateTime())
                        .orderType(EOrderType.fromString(result[3].toString()).orElse(null))
                        // Menggunakan metode fromString
                        .orderDetailResponses(new ArrayList<>())
                        .build();

                orderResponseMap.put(billId, orderResponse);
            }

            String orderDetailId = (String) result[4];

            MenuResponse menuResponse = MenuResponse.builder()
                    .menuId((String) result[6])
                    .menuPriceId((String) result[5])
                    .menuCode((String) result[7])
                    .menuName((String) result[8])
                    .status((String) result[9])
                    .price((Long) result[10])
                    .category(CategoryResponse.builder()
                            .id((String) result[11])
                            .categoryCode((String) result[12])
                            .categoryName((String) result[13])
                            .build())
                    .build();

            OrderDetailResponse orderDetailResponse = OrderDetailResponse.builder()
                    .orderDetailId(orderDetailId)
                    .menuResponse(menuResponse)
                    .quantity((Integer) result[14])
                    .build();

            orderResponse.getOrderDetailResponses().add(orderDetailResponse);
        }

        return new ArrayList<>(orderResponseMap.values());
    }



//tanpa native query
//    @Override
//    public OrderResponse createNewOrder(OrderRequest orderRequest) {
//        //Todo 1: convert orderDetailRequest to orderDetail
//        List<OrderDetail> orderDetails = orderRequest.getBillDetails().stream()
//                .map(orderDetailRequest -> {
//
//                //Todo 3: validasi product price
//                MenuPrice menuPrice = menuPriceService.getById(orderDetailRequest.getMenuPriceId());
//                return OrderDetail.builder()
//                        .menuPrice(menuPrice)
//                        .quantity(orderDetailRequest.getQuantity())
//                        .build();
//        }).toList();
//
//        String categoryCode = "CC";
//        int currentYear = LocalDateTime.now().getYear();
//        int sequenceNumber = 1;
//
//        // Menggunakan nilai transactionType langsung dari TransactionRequest transaction type
//        EOrderType orderType = orderRequest.getOrderType();
//
//        String receiptNumber = generateReceiptNumber(categoryCode, currentYear, sequenceNumber);
//
//        // Todo 4: Create New transaction
//       Order order = Order.builder()
//                .receiptNumber(receiptNumber)
//                .transDate(LocalDateTime.now())
//                .orderType(orderType)
//                .orderDetails(orderDetails)
//                .build();
//        orderRepository.saveAndFlush(order);
//
//        List<OrderDetailResponse> orderDetailResponses = order.getOrderDetails().stream().map(orderDetail -> {
//            //Todo 5: set transaction dari transactionDetail setelah membuat transaction baru
//            orderDetail.setOrder(order);
//            System.out.println(order);
//
//            //Todo 6: respon trancsationdetail
//            MenuPrice currentMenuPrice = orderDetail.getMenuPrice();
//            return OrderDetailResponse.builder()
//                    .orderDetailId(orderDetail.getId())
//                    .quantity(orderDetail.getQuantity())
//
//                    //Todo 7: convert product ke productResponse(productPrice)
//                    .menuResponse(MenuResponse.builder()
//                            .menuId(currentMenuPrice.getMenu().getId())
//                            .menuPriceId(currentMenuPrice.getId())
//                            .menuCode(currentMenuPrice.getMenu().getMenuCode())
//                            .menuName(currentMenuPrice.getMenu().getMenuName())
//                            .status(currentMenuPrice.getMenu().getStatus())
//                            .price(currentMenuPrice.getPrice())
//                            .category(CategoryResponse.builder()
//                                    .id(currentMenuPrice.getCategory().getId())
//                                    .categoryCode(currentMenuPrice.getCategory().getCategoryCode())
//                                    .categoryName(currentMenuPrice.getCategory().getCategoryName())
//                                    .build())
//                            .build())
//                    .build();
//        }).toList();
//
//
//        //todo: 10. return orderResponse
//        return OrderResponse.builder()
//                .billId(order.getId())
//                .receiptNumber(order.getReceiptNumber())
//                .transDate(order.getTransDate())
//                .orderType(order.getOrderType())
//                .orderDetailResponses(orderDetailResponses)
//                .build();
//    }


//tanpa native query
//    @Override
//    public OrderResponse getOrderById(String id) {
//        // todo 1 : Mendapatkan transaksi dari repository berdasarkan ID
//       Order order = orderRepository.findById(id).orElse(null);
//
//        if (order != null) {
//            //todo 2:  Operasi pemetaan untuk mengonversi transaksi dan detail transaksi ke Response
//            List<OrderDetailResponse> orderDetailResponses = order.getOrderDetails().stream()
//                    .map(orderDetail -> {
//                        MenuPrice currentMenuPrice = orderDetail.getMenuPrice();
//                        return OrderDetailResponse.builder()
//                                .orderDetailId(orderDetail.getId())
//                                .quantity(orderDetail.getQuantity())
//                                .menuResponse(MenuResponse.builder()
//                                        .menuId(currentMenuPrice.getMenu().getId())
//                                        .menuPriceId(currentMenuPrice.getId())
//                                        .menuCode(currentMenuPrice.getMenu().getMenuCode())
//                                        .menuName(currentMenuPrice.getMenu().getMenuName())
//                                        .status(currentMenuPrice.getMenu().getStatus())
//                                        .price(currentMenuPrice.getPrice())
//                                        .category(CategoryResponse.builder()
//                                                .id(currentMenuPrice.getCategory().getId())
//                                                .categoryCode(currentMenuPrice.getCategory().getCategoryCode())
//                                                .categoryName(currentMenuPrice.getCategory().getCategoryName())
//                                                .build())
//                                        .build())
//                                .build();
//                    })
//                    .toList();
//
//            //todo 3 : Operasi pemetaan untuk mengonversi transaksi ke Response
//            return OrderResponse.builder()
//                    .billId(order.getId())
//                    .receiptNumber(order.getReceiptNumber())
//                    .transDate(order.getTransDate())
//                    .orderType(order.getOrderType())
//                    .orderDetailResponses(orderDetailResponses)
//                    .build();
//        } else {
//            return null;
//        }
//    }


    //tanpa native query
//    @Override
//    public List<OrderResponse> getAllOrder() {
//        return orderRepository.findAll().stream()
//                .map(order -> {
//                    List<OrderDetailResponse> orderDetailResponses = order.getOrderDetails().stream()
//                            .map(orderDetail -> {
//                                //Todo 1: response order detail
//                                MenuPrice currentMenuPrice = orderDetail.getMenuPrice();
//                                return OrderDetailResponse.builder()
//                                        .orderDetailId(orderDetail.getId())
//                                        .quantity(orderDetail.getQuantity())
//                                        //Todo 7: convert product ke productResponse(productPrice)
//                                        .menuResponse(MenuResponse.builder()
//                                                .menuId(currentMenuPrice.getMenu().getId())
//                                                .menuPriceId(currentMenuPrice.getId())
//                                                .menuCode(currentMenuPrice.getMenu().getMenuCode())
//                                                .menuName(currentMenuPrice.getMenu().getMenuName())
//                                                .status(currentMenuPrice.getMenu().getStatus())
//                                                .price(currentMenuPrice.getPrice())
//                                                .category(CategoryResponse.builder()
//                                                        .id(currentMenuPrice.getCategory().getId())
//                                                        .categoryCode(currentMenuPrice.getCategory().getCategoryCode())
//                                                        .categoryName(currentMenuPrice.getCategory().getCategoryName())
//                                                        .build())
//                                                .build())
//                                        .build();
//                            })
//                            .toList();
//
//                    return OrderResponse.builder()
//                            .billId(order.getId())
//                            .receiptNumber(order.getReceiptNumber())
//                            .transDate(order.getTransDate())
//                            .orderType(order.getOrderType())
//                            .orderDetailResponses(orderDetailResponses)
//                            .build();
//                })
//                .collect(Collectors.toList());
//        }
}
