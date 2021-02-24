package smilyk.homeacc.service.inputCard;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import smilyk.homeacc.constants.BillConstants;
import smilyk.homeacc.constants.OutputCardConstant;
import smilyk.homeacc.dto.OutputCardDto;
import smilyk.homeacc.enums.Currency;
import smilyk.homeacc.model.Bill;
import smilyk.homeacc.model.OutputCard;
import smilyk.homeacc.repo.BillRepository;
import smilyk.homeacc.repo.InputCardRepository;
import smilyk.homeacc.service.user.UserServiceImpl;
import smilyk.homeacc.utils.Utils;

@Service
public class InputCardServiceImpl implements InputCardService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
    ModelMapper modelMapper = new ModelMapper();
    @Autowired
    InputCardRepository inputCardRepository;
    @Autowired
    Utils utils;
    @Autowired
    BillRepository billRepository;

    @Override
    public OutputCardDto createInputCard(OutputCardDto outputCardDto) {
        Bill bill = billRepository.findByBillNameAndUserUuidAndDeleted(outputCardDto.getBillName(),
            outputCardDto.getUserUuid(), false).get();
        billRepository.delete(bill);
        bill = changeSum(outputCardDto, bill);
        billRepository.save(bill);
        LOGGER.info(BillConstants.BILL_SUM + BillConstants.FOR + BillConstants.BILL_WITH_NAME
        +bill.getBillName() + BillConstants.CHANGED);
        OutputCard outputCard = getInputCard(outputCardDto);
        inputCardRepository.save(outputCard);
        LOGGER.info(OutputCardConstant.OUTPUT_CARD + outputCardDto.getUserUuid() +
            OutputCardConstant.CREATED);
        return modelMapper.map(outputCard, OutputCardDto.class);
    }

    private Bill changeSum(OutputCardDto outputCardDto, Bill bill) {
        if (outputCardDto.getCurrency().equals(Currency.USA)) {
            bill.setSumUsa(bill.getSumUsa() - outputCardDto.getSum());
        }
        else if (outputCardDto.getCurrency().equals(Currency.ISR)) {
            bill.setSumIsr(bill.getSumIsr() - outputCardDto.getSum());
        }
        else if (outputCardDto.getCurrency().equals(Currency.UKR)) {
            bill.setSumUkr(bill.getSumUkr() - outputCardDto.getSum());
        }
        return bill;
    }

    private OutputCard getInputCard(OutputCardDto outputCardDto) {
        OutputCard outputCard = OutputCard.builder()
            .deleted(false)
            .subcategoryUuid(outputCardDto.getSubCategoryUuid())
            .subcategoryName(outputCardDto.getSubCategoryName())
            .categoryUuid(outputCardDto.getCategoryUuid())
            .categoryName(outputCardDto.getCategoryName())
            .billName(outputCardDto.getBillName())
            .billUuid(outputCardDto.getBillUuid())
            .inputCardUuid(utils.generateUserUuid().toString())
            .count(outputCardDto.getCount())
            .currency(outputCardDto.getCurrency())
            .userUuid(outputCardDto.getUserUuid())
            .discount(outputCardDto.getDiscount())
            .note(outputCardDto.getNote())
            .sum(outputCardDto.getSum())
            .unit(outputCardDto.getUnit())
            .build();
        outputCard.setInputCardUuid(utils.generateUserUuid().toString());
        outputCard.setDeleted(false);
        return outputCard;
    }
}
