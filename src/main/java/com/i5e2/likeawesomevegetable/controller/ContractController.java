package com.i5e2.likeawesomevegetable.controller;

import com.i5e2.likeawesomevegetable.domain.contract.ContractService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/api/v1/contract")
@RequiredArgsConstructor
@Slf4j
public class ContractController {

    private final ContractService contractService;

    // 1. 모집 (기업 서명 전, 계약서 생성)
    @GetMapping("/{buyingId}/{applyId}/new-gather-contract")
    public ModelAndView getBuyingNewContract(@PathVariable Long buyingId, @PathVariable Long applyId) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, ParseException, InvalidKeyException {

        ModelAndView mv = new ModelAndView("/contract/gather-new-contract");

        String accessToken = contractService.getAccessToken();
        String refreshToken = contractService.getRefreshToken();

        mv.addObject("accessToken", accessToken);
        mv.addObject("refreshToken", refreshToken);

        HashMap data = contractService.getBuyingNewContract(buyingId, applyId);
        mv.addObject("data", data);

        return mv;
    }


    // 2. 경매 (농가 서명 전, 계약서 생성)
    @GetMapping("/{auctionId}/{biddingId}/new-auction-contract")
    public ModelAndView getAuctionNewContract(@PathVariable Long auctionId, @PathVariable Long biddingId) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, ParseException, InvalidKeyException {
        ModelAndView mv = new ModelAndView("/contract/auction-new-contract");

        String accessToken = contractService.getAccessToken();
        String refreshToken = contractService.getRefreshToken();

        mv.addObject("accessToken", accessToken);
        mv.addObject("refreshToken", refreshToken);

        HashMap data = contractService.getAuctionNewContract(auctionId, biddingId);
        mv.addObject("data", data);

        return mv;
    }

    @PostMapping("/saveContractDB")
    public void saveContractDB(@RequestBody Map<String, String> request) {
        log.info("요청온 데이터:{}", String.valueOf(request.get("documentId")));

        contractService.saveContractDB(request);
    }

    @GetMapping("/mail-success")
    public String getMailSend() {
        return "contract/mail-success";
    }

    // 계약 진행 상황 조회
    @GetMapping("{auctionId}/{documentId}/status")
    public void getStatus(@PathVariable Long auctionId,@PathVariable String documentId) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, ParseException, InvalidKeyException {
        contractService.getContractInfo(documentId, auctionId);
    }
}
