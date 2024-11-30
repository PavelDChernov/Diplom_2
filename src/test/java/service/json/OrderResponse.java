package service.json;

public class OrderResponse {
    private String name;
    private OrderNumber order;
    private Boolean success;

    public OrderResponse() {
    }

    public OrderResponse(String name, OrderNumber order, Boolean success) {
        this.name = name;
        this.order = order;
        this.success = success;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OrderNumber getOrder() {
        return order;
    }

    public void setOrder(OrderNumber order) {
        this.order = order;
    }

    public Boolean isSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }
}
