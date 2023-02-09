package com.i5e2.likeawesomevegetable.domain.market;

import com.i5e2.likeawesomevegetable.domain.user.CompanyUser;
import com.i5e2.likeawesomevegetable.domain.user.FarmUser;
import com.i5e2.likeawesomevegetable.domain.user.User;
import com.i5e2.likeawesomevegetable.domain.user.file.exception.FileErrorCode;
import com.i5e2.likeawesomevegetable.domain.user.file.exception.FileException;
import com.i5e2.likeawesomevegetable.repository.CompanyBuyingJpaRepository;
import com.i5e2.likeawesomevegetable.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BuyingService {
    private final CompanyBuyingJpaRepository buyingJpaRepository;
    private final UserJpaRepository userJpaRepository;

    public String creatBuyingNoneAuth(BuyingRequest buyingRequest) {

        CompanyBuying companyBuying = buyingRequest.toEntityNoneAuth(buyingRequest);
        buyingJpaRepository.save(companyBuying);

        return null;
    }

    public BuyingResponse creatBuying(BuyingRequest buyingRequest, String email) {
        User user = userJpaRepository.findByEmail(email).get();
        CompanyUser companyUser = user.getCompanyUser();

        notValidCompanyUser(companyUser);

        CompanyBuying companyBuying = buyingRequest.toEntity(buyingRequest, companyUser);
        buyingJpaRepository.save(companyBuying);

        BuyingResponse buyingResponse = BuyingResponse.builder()
                .buyingId(companyBuying.getId())
                .message("모집 게시글 작성 완료")
                .build();
        return buyingResponse;
    }


    private void notValidCompanyUser(CompanyUser companyUser) {
        if (companyUser==null) {
            throw new FileException(
                    FileErrorCode.COMPANY_USER_NOT_FOUND,
                    FileErrorCode.COMPANY_USER_NOT_FOUND.getMessage()
            );
        }
    }
}
