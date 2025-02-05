package inside_payment.entity;

import inside_payment.tars.insidepayment.InsidePaymentTars;
import inside_payment.tars.insidepayment.PaymentTypeTars;
import inside_payment.tars.rpc.payment.PaymentTars;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.UUID;


@Document(collection="payment")
public class Payment {
    @Id
    @NotNull
    @Valid
    private String id;

    @NotNull
    @Valid
    private String orderId;

    @NotNull
    @Valid
    private String userId;

    @NotNull
    @Valid
    private String price;

    @NotNull
    @Valid
    private PaymentType type;

    public Payment(){
        this.id = UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public PaymentType getType() {
        return type;
    }

    public void setType(PaymentType type) {
        this.type = type;
    }

    public InsidePaymentTars toTars(){
        InsidePaymentTars paymentTars = new InsidePaymentTars();
        paymentTars.setId(this.id);
        paymentTars.setOrderId(this.orderId);
        paymentTars.setPrice(this.price);
        paymentTars.setUserId(this.userId);
        PaymentTypeTars paymentTypeTars = new PaymentTypeTars();
        paymentTypeTars.setIndex(this.type.getIndex());
        paymentTypeTars.setName(this.type.getName());
        paymentTars.setType(paymentTypeTars);
        return paymentTars;

    }

}
