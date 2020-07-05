package eu.acme.demo.web.dto;

import java.math.BigDecimal;
import java.util.List;

public class OrderRequest {

    private String clientReferenceCode;
    private String description;
    private List<OrderItem> orderItems;

    public String getClientReferenceCode() {
        return clientReferenceCode;
    }

    public void setClientReferenceCode(String clientReferenceCode) {
        this.clientReferenceCode = clientReferenceCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public BigDecimal getTotalAmount() {
        BigDecimal result = BigDecimal.ZERO;
        for (OrderItem orderItem : orderItems) {
            result = result.add(new BigDecimal(orderItem.getUnitPrice()));
            result = result.multiply(new BigDecimal(orderItem.getUnits()));
        }
        return result;
    }

    public static class OrderItem {

        private String unitPrice;
        private int units;

        public String getUnitPrice() {
            return unitPrice;
        }

        public void setUnitPrice(String unitPrice) {
            this.unitPrice = unitPrice;
        }

        public int getUnits() {
            return units;
        }

        public void setUnits(int units) {
            this.units = units;
        }
    }
}
