package service.json;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OrdersResponse {
    private Boolean success;
    private List<Order> orders;
    private Integer total;
    private Integer totalToday;
}
