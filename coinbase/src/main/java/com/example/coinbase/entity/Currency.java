package com.example.coinbase.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString(exclude = {"createdDate", "updatedDate"})
@Entity
@Table(name = "currency")
@EntityListeners(AuditingEntityListener.class)
public class Currency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(lombok.AccessLevel.NONE)
    private Long id;

    @NotBlank(message = "幣別代碼不能為空")
    @Pattern(regexp = "^[A-Z]{3}$", message = "幣別代碼必須是3個大寫字母")
    @Column(name = "code", nullable = false, unique = true)
    private String code;
    
    @NotBlank(message = "中文名稱不能為空")
    @Size(min = 2, max = 10, message = "中文名稱長度必須在2-10之間")
    @Column(name = "chinese_name", nullable = false)
    private String chineseName;
    
    @CreatedDate
    @Column(name = "created_date", updatable = false)
    @Setter(lombok.AccessLevel.NONE)
    private LocalDateTime createdDate;
    
    @LastModifiedDate
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Currency)) return false;
        Currency currency = (Currency) o;
        return code != null && code.equals(currency.code);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
} 