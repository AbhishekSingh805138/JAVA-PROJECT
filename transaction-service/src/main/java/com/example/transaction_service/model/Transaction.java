package com.example.transaction_service.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer transactionId;

    public Integer getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Integer transactionId) {
		this.transactionId = transactionId;
	}

	public Integer getFrmAccountId() {
		return frmAccountId;
	}

	public void setFrmAccountId(Integer frmAccountId) {
		this.frmAccountId = frmAccountId;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public Integer getToAccountId() {
		return toAccountId;
	}

	public void setToAccountId(Integer toAccountId) {
		this.toAccountId = toAccountId;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public LocalDateTime getDateTransaction() {
		return dateTransaction;
	}

	public void setDateTransaction(LocalDateTime dateTransaction) {
		this.dateTransaction = dateTransaction;
	}

	private Integer frmAccountId;     // From which account (nullable for DEPOSIT)
    private String transactionType;   // DEPOSIT, WITHDRAW, TRANSFER
    private Integer toAccountId;      // Target account (nullable if not TRANSFER)

    private Double amount;            // Better as Double for money handling
    private Integer userId;           // Reference to User

    private LocalDateTime dateTransaction;

    @PrePersist
    protected void onCreate() {
        this.dateTransaction = LocalDateTime.now();
    }
}
