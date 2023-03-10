package com.i5e2.likeawesomevegetable.farm.auction.repository;

import com.i5e2.likeawesomevegetable.board.post.dto.ParticipationStatus;
import com.i5e2.likeawesomevegetable.farm.auction.FarmAuction;
import com.i5e2.likeawesomevegetable.farm.mypage.dto.FarmAuctionByUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FarmAuctionJpaRepository extends JpaRepository<FarmAuction, Long> {
    @Query(nativeQuery = true, value = "SELECT * FROM like_awesome_vegetable.t_farm_auction \n" +
            "where post_point_activate = 'ABLE'")
    List<FarmAuction> findAllByPostPointActivate(Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT * FROM like_awesome_vegetable.t_farm_auction t\n" +
            "LEFT OUTER JOIN like_awesome_vegetable.t_farm_auction_like tt ON t.farm_auction_id = tt.farm_auction_id\n" +
            "WHERE post_point_activate = 'ABLE'\n" +
            "GROUP BY t.farm_auction_id\n" +
            "ORDER BY count(tt.farm_auction_id) DESC")
    List<FarmAuction> findAllByPostPointActivatewithHot(Pageable pageable);

    @Query(value = "select farm.id as farmAuctionId" +
            ", farm.auctionTitle as auctionTitle" +
            ", farm.auctionItem as auctionItemName" +
            ", farm.farmUser.farmAddress as farmAddress" +
            ", farm.auctionStartPrice as auctionStartPrice" +
            ", farm.auctionRegisteredAt as auctionRegisteredAt" +
            ", farm.postPointActivate as postPointActivate " +
            "from FarmAuction farm " +
            "where farm.farmUser.id = :farmUserId")
    List<FarmAuctionByUser> findByFarmAuctions(Long farmUserId, Pageable pageable);

    Page<FarmAuction> findAllByFarmUserIdAndParticipationStatus(Long farmId,
                                                                ParticipationStatus participationStatus, Pageable pageable);
}
