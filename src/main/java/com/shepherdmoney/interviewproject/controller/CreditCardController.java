package com.shepherdmoney.interviewproject.controller;

import com.shepherdmoney.interviewproject.model.BalanceHistory;
import com.shepherdmoney.interviewproject.model.CreditCard;
import com.shepherdmoney.interviewproject.model.User;
import com.shepherdmoney.interviewproject.repository.CreditCardRepository;
import com.shepherdmoney.interviewproject.repository.UserRepository;
import com.shepherdmoney.interviewproject.utils.Utils;
import com.shepherdmoney.interviewproject.vo.request.AddCreditCardToUserPayload;
import com.shepherdmoney.interviewproject.vo.request.UpdateBalancePayload;
import com.shepherdmoney.interviewproject.vo.response.CreditCardView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
public class CreditCardController {

    // TODO: wire in CreditCard repository here (~1 line)
    @Autowired
    private CreditCardRepository creditCardRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/credit-card")
    public ResponseEntity<Integer> addCreditCardToUser(@RequestBody AddCreditCardToUserPayload payload) {
        // TODO: Create a credit card entity, and then associate that credit card with user with given userId
        //       Return 200 OK with the credit card id if the user exists and credit card is successfully associated with the user
        //       Return other appropriate response code for other exception cases
        //       Do not worry about validating the card number, assume card number could be any arbitrary format and length

        // destructure teh payload
        int userId = payload.getUserId();
        String cardNumber = payload.getCardNumber();
        String cardIssuanceBank = payload.getCardIssuanceBank();

        // validate the payload parameter
        if (!Utils.isValidRequestBodyParam(cardNumber) || !Utils.isValidRequestBodyParam(cardIssuanceBank)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(-1);
        }

        // the card number should be unique
        if (creditCardRepository.existsByNumber(cardNumber)) {
            // card already exists
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(-1);
        }

        // query the user instance from database
        User user = userRepository.findById(userId).orElse(null);

        // validate the userId
        if (user == null) {
            // user does not exist
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(-1);
        }

        // create the credit card instance
        CreditCard creditCard = new CreditCard();
        creditCard.setNumber(cardNumber);
        creditCard.setIssuanceBank(cardIssuanceBank);

        // associate that credit card with user
        user.getCreditCards().add(creditCard);
        creditCard.setOwner(user);

        // we have set the CASCADE, so we only need to save one entity here
        // then the associated entity will be saved automatically
        // we save the credit card rather than the user,
        // because we cannot get the credit card id if saving the user
        creditCardRepository.save(creditCard);

        return ResponseEntity.status(HttpStatus.OK).body(creditCard.getId());
    }

    @GetMapping("/credit-card:all")
    public ResponseEntity<List<CreditCardView>> getAllCardOfUser(@RequestParam int userId) {
        // TODO: return a list of all credit card associated with the given userId, using CreditCardView class
        //       if the user has no credit card, return empty list, never return null

        // query the user instance from database
        User user = userRepository.findById(userId).orElse(null);

        // validate the userId
        if (user == null) {
            // user does not exist
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ArrayList<>());
        }

        // query the CreditCards of this user
        Set<CreditCard> creditCards = user.getCreditCards();
        if (creditCards == null) {
            // if the user has no credit card, return empty list
            return ResponseEntity.status(HttpStatus.OK).body(new ArrayList<>());
        }

        // convert the CreditCard set to a list, and wrap each instance with CreditCardView
        List<CreditCardView> creditCardViewList = new ArrayList<>();
        for (CreditCard cc : creditCards) {
            creditCardViewList.add(new CreditCardView(cc.getIssuanceBank(), cc.getNumber()));
        }

        // response the list of CreditCardView
        return ResponseEntity.status(HttpStatus.OK).body(creditCardViewList);
    }

    @GetMapping("/credit-card:user-id")
    public ResponseEntity<Integer> getUserIdForCreditCard(@RequestParam String creditCardNumber) {
        // TODO: Given a credit card number, efficiently find whether there is a user associated with the credit card
        //       If so, return the user id in a 200 OK response. If no such user exists, return 400 Bad Request

        // validate the request parameter
        if (!Utils.isValidRequestBodyParam(creditCardNumber)) {
            // if the creditCardNumber is null or empty string
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(-1);
        }

        // query the creditCard instance by creditCardNumber
        CreditCard creditCard = creditCardRepository.findByNumber(creditCardNumber).orElse(null);
        // check if the creditCardNumber exists
        if (creditCard == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(-1);
        }

        // get the owner of this credit card
        User owner = creditCard.getOwner();
        // check if the owner exist
        if (owner == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(-1);
        }

        // check if the owner id still exist in the user table
        if (!userRepository.existsById(owner.getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(-1);
        }

        // return the user id in a 200 OK response
        return ResponseEntity.status(HttpStatus.OK).body(owner.getId());
    }


//    @PostMapping("/credit-card:update-balance")
//    public SomeEnityData postMethodName(@RequestBody UpdateBalancePayload[] payload) {
//        //TODO: Given a list of transactions, update credit cards' balance history.
//        //      1. For the balance history in the credit card
//        //      2. If there are gaps between two balance dates, fill the empty date with the balance of the previous date
//        //      3. Given the payload `payload`, calculate the balance different between the payload and the actual balance stored in the database
//        //      4. If the different is not 0, update all the following budget with the difference
//        //      For example: if today is 4/12, a credit card's balanceHistory is [{date: 4/12, balance: 110}, {date: 4/10, balance: 100}],
//        //      Given a balance amount of {date: 4/11, amount: 110}, the new balanceHistory is
//        //      [{date: 4/12, balance: 120}, {date: 4/11, balance: 110}, {date: 4/10, balance: 100}]
//        //      Return 200 OK if update is done and successful, 400 Bad Request if the given card number
//        //        is not associated with a card.
//
//        return null;
//    }

    @PostMapping("/credit-card:update-balance")
    public ResponseEntity<String> updateBalanceHistoriesByTransactions(@RequestBody UpdateBalancePayload[] payloads) {
        //TODO: Given a list of transactions, update credit cards' balance history.
        //      1. For the balance history in the credit card
        //      2. If there are gaps between two balance dates, fill the empty date with the balance of the previous date
        //      3. Given the payload `payload`, calculate the balance different between the payload and the actual balance stored in the database
        //      4. If the different is not 0, update all the following budget with the difference
        //      For example: if today is 4/12, a credit card's balanceHistory is [{date: 4/12, balance: 110}, {date: 4/10, balance: 100}],
        //      Given a balance amount of {date: 4/11, amount: 110}, the new balanceHistory is
        //      [{date: 4/12, balance: 120}, {date: 4/11, balance: 110}, {date: 4/10, balance: 100}]
        //      Return 200 OK if update is done and successful, 400 Bad Request if the given card number
        //        is not associated with a card.

        // return 400 bad request if any given card number is not associated with a card
        for (UpdateBalancePayload payload : payloads) {
            String creditCardNumber = payload.getCreditCardNumber();
            if (!creditCardRepository.existsByNumber(creditCardNumber)){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("not all card numbers associated with a card");
            }
        }

        //
        for (UpdateBalancePayload payload : payloads) {
            String creditCardNumber = payload.getCreditCardNumber();
            LocalDate balanceDate = payload.getBalanceDate();
            double balanceAmount = payload.getBalanceAmount();

            // query the credit card instance, it will not be null
            CreditCard creditCard = creditCardRepository.findByNumber(creditCardNumber).get();
            Map<LocalDate, BalanceHistory> balanceHistories = creditCard.getBalanceHistories();

            // for test
            System.out.println(balanceHistories.toString());

            // fill the gaps with the previous data

        }

        return null;
    }
    
}
