package lk.ijse.dep11.pos.tm;

import com.jfoenix.controls.JFXButton;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    String code;
    String description;
    int qty;
    BigDecimal unitPrice;
    JFXButton btnDelete;
}
