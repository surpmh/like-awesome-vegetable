package com.i5e2.likeawesomevegetable.company.buying;

import com.i5e2.likeawesomevegetable.board.post.dto.ParticipationStatus;
import com.i5e2.likeawesomevegetable.board.post.dto.PostPointActivateEnum;
import com.i5e2.likeawesomevegetable.user.company.CompanyUser;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "t_company_buying")
public class CompanyBuying {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "company_buying_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_user_id")
    private CompanyUser companyUser;

    @Column(name = "buying_title", length = 50)
    private String buyingTitle;

    @Column(name = "buying_start_time")
    private String buyingStartTime;

    @Column(name = "buying_end_time")
    private String buyingEndTime;

    @Column(name = "buying_item_category")
    private String buyingItemCategory;

    @Column(name = "buying_item")
    private String buyingItem;

    @Column(name = "buying_quantity")
    private Integer buyingQuantity;

    @Column(name = "buying_price")
    private Integer buyingPrice;

    @Column(name = "buying_description", length = 5000)
    private String buyingDescription;

    @Column(name = "buying_shipping")
    private String buyingShipping;

    @Column(name = "receiver_name", length = 10)
    private String receiverName;

    @Column(name = "receiver_phone_no", length = 20)
    private String receiverPhoneNo;

    @Column(name = "receiver_address", length = 300)
    private String receiverAddress;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "company_buying_status")
    private ParticipationStatus participationStatus;

    @CreatedDate
    @Column(name = "buying_registered_at", updatable = false)
    private String buyingRegisteredAt;

    @LastModifiedDate
    @Column(name = "buying_modified_at")
    private String buyingModifiedAt;

    @Column(name = "buying_deleted_at")
    private LocalDateTime buyingDeletedAt;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "post_point_activate")
    private PostPointActivateEnum postPointActivate;

    @PrePersist
    public void onPrePersist() {
        this.buyingRegisteredAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.buyingModifiedAt = this.buyingRegisteredAt;
    }

    @PreUpdate
    public void onPreUpdatePersist() {
        this.buyingModifiedAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public void updatePostActivate(PostPointActivateEnum postPointActivate) {
        this.postPointActivate = postPointActivate;
    }

    public void updateStatusToEnd() {
        this.participationStatus = ParticipationStatus.END;
    }
}