package com.valcon.dataacademy.service;

import com.valcon.dataacademy.dao.IOrderRepository;
import com.valcon.dataacademy.exception.InvalidOrderException;
import com.valcon.dataacademy.model.Delivery;
import com.valcon.dataacademy.model.Order;
import com.valcon.dataacademy.model.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService implements IOrderService {

    @Autowired
    IOrderRepository orderRepo;

    IShippingService shippingService;

    @Override
    public List<Order> getOrderList() {
        return orderRepo.findAll();
    }

    @Override
    public Order getOrderById(Long id) {
        Order order = orderRepo.findById(id).get();

        Delivery deliveryDetails = shippingService.getDeliveryDetails(order);
        order.setDeliveryDetails(deliveryDetails);
        return order;
    }

    @Autowired
    public void setShippingService(IShippingService shippingService) {
        this.shippingService = shippingService;
    }

    @Override
    public Order saveOrder(Order order) {
        // validate
        if (order.getCustomerName() == null || order.getCustomerName().length() == 0) {
            throw new InvalidOrderException("Order does not have a customer name");
        }

        // join the orderItems to the order
        for (OrderItem item : order.getItems()) {
            item.setCustomerOrder(order);
        }

        Order result = this.orderRepo.save(order);
        return result;
    }
}
