package smilyk.homeacc.service.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import smilyk.homeacc.constants.CategorySubcategoryConstant;
import smilyk.homeacc.constants.OutputCardConstant;
import smilyk.homeacc.constants.ValidatorConstants;
import smilyk.homeacc.dto.CategoryDto;
import smilyk.homeacc.dto.SubcategoryDto;
import smilyk.homeacc.enums.CategoryType;
import smilyk.homeacc.enums.Currency;
import smilyk.homeacc.exceptions.HomeaccException;
import smilyk.homeacc.model.*;
import smilyk.homeacc.repo.*;
import smilyk.homeacc.service.category.CategoryService;
import smilyk.homeacc.service.subcategory.SubcategoryService;
import smilyk.homeacc.service.user.UserServiceImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class ValidatorServiceImpl implements ValidatorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    UserRepository userRepository;

    @Autowired
    BillRepository billRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    SubcategoryRepository subcategoryRepository;

    @Autowired
    CategoryService categoryService;

    @Autowired
    SubcategoryService subcategoryService;

    @Autowired
    OutputCardRepository outputCardRepository;

    @Autowired
    InputCardRepository inputCardRepository;

    @Override
    public void checkUserUnique(String email) {
        LOGGER.info(ValidatorConstants.CHECK_USER_WITH_EMAIL + email);
        Optional<User> user = userRepository.findByEmailAndDeleted(email, false);
        if (!user.isEmpty()) {
            LOGGER.error(ValidatorConstants.NOT_UNIQUE_USER + email);
            throw new HomeaccException(ValidatorConstants.NOT_UNIQUE_USER);
        }
    }

    @Override
    public void checkUserExists(String userUuid) {
        LOGGER.info(ValidatorConstants.CHECK_USER_WITH_UUIDL + userUuid);
        Optional<User> user = userRepository.findByUserUuidAndDeleted(userUuid, false);
        if (user.isEmpty()) {
            LOGGER.error(ValidatorConstants.USER_WITH_UUID + userUuid + ValidatorConstants.NOT_FOUND);
            throw new HomeaccException(ValidatorConstants.NOT_UNIQUE_USER + userUuid);
        }
    }

    @Override
    public void checkMainBillsForDeleted(String billName) {
        LOGGER.info(ValidatorConstants.CHECK_BILLS_FOR_DELETED);
//        dont check bill per user - checked it before
        Optional<Bill> billOptional = billRepository.findByBillNameAndDeleted(billName, false);
        if (billOptional.get().getMainBill()) {
            LOGGER.error(ValidatorConstants.MAIN_BILL + billName + ValidatorConstants.CHANGE_MAIN_BILL);
            throw new HomeaccException(ValidatorConstants.MAIN_BILL + billName + ValidatorConstants.CHANGE_MAIN_BILL);
        }
    }

    @Override
    public Category checkCategory(String categoryName, String userUuid) {
        Optional<Category> categoryOptional = categoryRepository.findByCategoryNameAndUserUuid(categoryName, userUuid);
        if (categoryOptional.isPresent()) {
            return categoryOptional.get();
        }
        CategoryDto categoryDto = CategoryDto.builder()
            .categoryName(categoryName)
            .userUuid(userUuid)
            .description("")
            .type(CategoryType.OUTPUT)
            .build();
        return categoryService.save(categoryDto);
    }

    @Override
    public Subcategory checkSubcategory(String subcategoryName, String userUuid) {
        Optional<Subcategory> subcategoryOptional = subcategoryRepository
            .findBySubcategoryNameAndUserUuid(subcategoryName, userUuid);
        if (subcategoryOptional.isPresent()) {
            return subcategoryOptional.get();
        }
        SubcategoryDto subcategoryDto = SubcategoryDto.builder()
            .subcategoryName(subcategoryName)
            .userUuid(userUuid)
            .description("")
            .build();
        return subcategoryService.save(subcategoryDto);
    }

    @Override
    public void checkCategoryByName(String categoryName, String userUuid){
        Optional<Category> categoryOptional = categoryRepository.findByCategoryNameAndUserUuid(categoryName, userUuid);
        if(categoryOptional.isPresent()){
            LOGGER.error(CategorySubcategoryConstant.CATEGORY_WITH_NAME + categoryName
                + CategorySubcategoryConstant.EXISTS);
            throw new HomeaccException(
                CategorySubcategoryConstant.CATEGORY_WITH_NAME + categoryName + CategorySubcategoryConstant.EXISTS
            );
        }
//        TODO test
    }

    @Override
    public void checkCategoryByNameForDeleted(String categoryUuid, String userUuid) {
        Optional<Category> categoryOptional = categoryRepository.findByCategoryUuidAndUserUuid(categoryUuid, userUuid);
        if(!categoryOptional.isPresent()){
            LOGGER.error(CategorySubcategoryConstant.CATEGORY_WITH_UUID + categoryUuid
                + CategorySubcategoryConstant.NOT_FOUND);
            throw new HomeaccException(
                CategorySubcategoryConstant.CATEGORY_WITH_UUID + categoryUuid + CategorySubcategoryConstant.NOT_FOUND
            );
        }
        //        TODO test
    }

    @Override
    public void checkSubcategoryByName(String subcategoryName, String userUuid) {
        Optional<Subcategory> subcategoryOptional = subcategoryRepository
            .findBySubcategoryNameAndUserUuid(subcategoryName, userUuid);
        if(subcategoryOptional.isPresent()){
            LOGGER.error(CategorySubcategoryConstant.SUBCATEGORY_WITH_NAME + subcategoryName
                + CategorySubcategoryConstant.EXISTS);
            throw new HomeaccException(
                CategorySubcategoryConstant.CATEGORY_WITH_NAME + subcategoryName + CategorySubcategoryConstant.EXISTS
            );
        }
//        TODO
    }

    @Override
    public void checkSubcategoryByUuidForDeleted(String subcategoryUuid, String userUuid) {
        Optional<Subcategory> subcategoryOptional = subcategoryRepository
            .findBySubcategoryUuidAndUserUuid(subcategoryUuid, userUuid);
        if(!subcategoryOptional.isPresent()){
            LOGGER.error(CategorySubcategoryConstant.SUBCATEGORY_WITH_UUID + subcategoryUuid
                + CategorySubcategoryConstant.NOT_FOUND);
            throw new HomeaccException(
                CategorySubcategoryConstant.CATEGORY_WITH_UUID + subcategoryUuid + CategorySubcategoryConstant.NOT_FOUND
            );
        }
        //        TODO test
    }

    @Override
    public void checkOutputForDeleted(String outputCardUuid) {
        Optional<OutputCard> optionalOutputCard = outputCardRepository.findByOutputCardUuid(outputCardUuid);
        if(!optionalOutputCard.isPresent()){
            LOGGER.error(OutputCardConstant.OUTPUT_CARD_WITH_UUID + outputCardUuid
            + OutputCardConstant.NOT_FOUND);
            throw new HomeaccException(OutputCardConstant.OUTPUT_CARD_WITH_UUID + outputCardUuid
                + OutputCardConstant.NOT_FOUND);
        }
    }

    @Override
    public void checkInputForDeleted(String inputCardUuid) {
        Optional<InputCard> optionalInputCard = inputCardRepository.findByInputCardUuid(inputCardUuid);
        if(!optionalInputCard.isPresent()){
            LOGGER.error(OutputCardConstant.INPUT_CARD_WITH_UUID + inputCardUuid
                + OutputCardConstant.NOT_FOUND);
            throw new HomeaccException(OutputCardConstant.INPUT_CARD_WITH_UUID + inputCardUuid
                + OutputCardConstant.NOT_FOUND);
        }
    }

    @Override
    public void checkUniqueBill(String billName) {
        LOGGER.info(ValidatorConstants.CHECK_BILL_BY_BILL_NAME + billName);
        Optional<Bill> bill = billRepository.findByBillNameAndDeleted(billName, false);
        if (!bill.isEmpty()) {
            LOGGER.error(ValidatorConstants.NOT_UNIQUE_BILL + billName);
            throw new HomeaccException(ValidatorConstants.NOT_UNIQUE_BILL + billName);
        }
    }

    @Override
    public void checkBill(String billName) {
        LOGGER.info(ValidatorConstants.CHECK_BILL_BY_BILL_NAME + billName);
        Optional<Bill> bill = billRepository.findByBillNameAndDeleted(billName, false);
        if (bill.isEmpty()) {
            LOGGER.error(ValidatorConstants.CHECK_BILL_BY_BILL_NAME + billName + ValidatorConstants.NOT_FOUND);
            throw new HomeaccException(ValidatorConstants.CHECK_BILL_BY_BILL_NAME + billName +
                ValidatorConstants.NOT_FOUND);
        }
    }

    @Override
    public void checkBillByUser(String billName, String userUuid) {
        LOGGER.info(ValidatorConstants.CHECK_BILL_BY_BILL_NAME_AND_USER + billName + " " + userUuid);
        Optional<Bill> bill = billRepository.findByBillNameAndUserUuidAndDeleted(billName, userUuid, false);
        if (bill.isEmpty()) {
            LOGGER.error(ValidatorConstants.CHECK_BILL_BY_BILL_NAME + billName + ValidatorConstants.FOR_USER + userUuid +
                ValidatorConstants.NOT_FOUND);
            throw new HomeaccException(ValidatorConstants.CHECK_BILL_BY_BILL_NAME + billName +
                ValidatorConstants.FOR_USER + userUuid +
                ValidatorConstants.NOT_FOUND);
        }
    }

    @Override
    public void checkBillByUserAndCurrency(String billName, String userUuid, Currency currency) {
        LOGGER.info(ValidatorConstants.CHECK_BILL_BY_BILL_NAME_AND_USER + billName + " " + userUuid +
            ValidatorConstants.AND_CURRENCY + currency.name());

        Optional<Bill> bill = billRepository.findByBillNameAndUserUuidAndDeleted(billName, userUuid, false);
        if (bill.isEmpty()) {
            LOGGER.error(ValidatorConstants.CHECK_BILL_BY_BILL_NAME + billName + ValidatorConstants.FOR_USER + userUuid +
                ValidatorConstants.NOT_FOUND);
            throw new HomeaccException(ValidatorConstants.CHECK_BILL_BY_BILL_NAME + billName +
                ValidatorConstants.FOR_USER + userUuid +
                ValidatorConstants.NOT_FOUND);
        }
        Currency billCurrencyRestored = bill.get().getCurrencyName();
        if (!billCurrencyRestored.equals(Currency.ALL)) {
            if (!billCurrencyRestored.equals(currency)
            ) {
                LOGGER.error( billName + ValidatorConstants.FOR_USER + userUuid +
                    ValidatorConstants.AND_CURRENCY + currency.name() +
                    ValidatorConstants.NOT_FOUND);
                throw new HomeaccException(billName +
                    ValidatorConstants.FOR_USER + userUuid +
                    ValidatorConstants.AND_CURRENCY + currency.name() +
                    ValidatorConstants.NOT_FOUND);
            }
        }
    }

    @SneakyThrows
    @Override
    public void checkCurrencyNameValid(String billsCurrency) {
        List<Currency> currency = Arrays.asList(Currency.values());

        try {
            Currency.valueOf(billsCurrency);
        } catch (Exception ex) {
            LOGGER.error(ValidatorConstants.CURRENCY_IS_WRONG + billsCurrency);

            throw new HomeaccException(ValidatorConstants.CURRENCY_IS_WRONG + ValidatorConstants.CHOOSE_CURRENCY +
                mapper.writeValueAsString(currency)
            );
        }
    }


    @Override
    public void checkMainBill(Boolean mainBill) {
        if (mainBill) {
            Optional<Bill> bill = billRepository.findByMainBill(true);
            if (!bill.isEmpty()) {
                LOGGER.info(ValidatorConstants.ONE_MAIN_BILL);
                throw new HomeaccException(ValidatorConstants.ONE_MAIN_BILL);
            }
        }
    }

}
