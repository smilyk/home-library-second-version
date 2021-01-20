package smilyk.homeacc.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import smilyk.homeacc.dto.BillDto;
import smilyk.homeacc.dto.OperationStatuDto;
import smilyk.homeacc.dto.UserDto;
import smilyk.homeacc.enums.Currency;
import smilyk.homeacc.enums.RequestOperationName;
import smilyk.homeacc.enums.RequestOperationStatus;
import smilyk.homeacc.service.bill.BillService;
import smilyk.homeacc.service.validation.ValidatorService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class BillControllerTest {
    @InjectMocks
    BillController billController;

    @Mock
    BillService billService;

    @Mock
    ValidatorService validatorService;
    private BillDto mainBillDto;
    private BillDto notMainBillDto;

    private static final String USER_UUID = "1212";
    private static final String BILL_NAME = "BILL_NAME";
    private static final String BILL_UUID = "1111";
    private static final Currency All_CURRENCY_NAME = Currency.ALL;
    private static final Double SUMM_ISR = 100.0;
    private static final Double SUMM_UKR = 20.0;
    private static final Double SUMM_USA = 130.0;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        mainBillDto = BillDto.builder()
                .billName(BILL_NAME)
                .billUuid(BILL_UUID)
                .currencyName(All_CURRENCY_NAME)
                .mainBill(true)
                .description("")
                .sumIsr(SUMM_ISR)
                .sumUkr(SUMM_UKR)
                .sumUsa(SUMM_USA)
                .userUuid(USER_UUID)
                .build();
        notMainBillDto = BillDto.builder()
                .billName(BILL_NAME + "notMain")
                .billUuid(BILL_UUID+"notMain")
                .currencyName(Currency.USA)
                .mainBill(true)
                .description("")
                .sumIsr(SUMM_ISR)
                .sumUkr(SUMM_UKR)
                .sumUsa(SUMM_USA)
                .userUuid(USER_UUID)
                .build();
    }

    @Test
    void createBill() {
        when(billService.createBill(any(BillDto.class))).thenReturn(mainBillDto);
        BillDto restoredBillDto = billController.createBill(mainBillDto);

        assertNotNull(restoredBillDto);
        assertEquals(mainBillDto, restoredBillDto);
    }

    @Test
    void getBillByBillName() {
        when(billService.getBillByBillName(anyString(), anyString())).thenReturn(mainBillDto);
        BillDto restoredBillDto = billController.getBillByBillName(BILL_NAME, USER_UUID);

        assertNotNull(restoredBillDto);
        assertEquals(mainBillDto, restoredBillDto);
    }

    @Test
    void getAllBillsByUserUuid() {
        List<BillDto> billDtoList = Arrays.asList(mainBillDto);
        when(billService.getAllBillsByUser( anyString())).thenReturn(billDtoList);
        List<BillDto> restoredBillDto = billController.getAllBillsByUserUuid(USER_UUID);

        assertNotNull(restoredBillDto);
        assertEquals(1, restoredBillDto.size());
    }

    @Test
    void getAllBillsByUserUuidAndCurrencyAll() {
        List<BillDto> billDtoList = Arrays.asList(mainBillDto);
        when(billService.getAllBillsByUserUuidAndCurrency( anyString(), anyString())).thenReturn(billDtoList);
        List<BillDto> restoredBillDto = billController.getAllBillsByUserUuidAndCurrency(USER_UUID, Currency.ALL.name());

        assertNotNull(restoredBillDto);
        assertEquals(1, restoredBillDto.size());
    }

    @Test
    void getAllBillsByUserUuidAndCurrencyUSA() {
        List<BillDto> billDtoList = Arrays.asList(mainBillDto, notMainBillDto);
        when(billService.getAllBillsByUserUuidAndCurrency( anyString(), anyString())).thenReturn(billDtoList);
        List<BillDto> restoredBillDto = billController.getAllBillsByUserUuidAndCurrency(USER_UUID, Currency.USA.name());

        assertNotNull(restoredBillDto);
        assertEquals(2, restoredBillDto.size());
    }

    @Test
    void changeMailBillWithMainBill() {
        when(billService.changeMailBill(anyString())).thenReturn(mainBillDto);
        BillDto restoredBillDto =  billController.changeMailBill(BILL_NAME, USER_UUID);

        assertNotNull(restoredBillDto);
        assertEquals(true, restoredBillDto.getMainBill());
    }

    @Test
    void changeMailBillWithNotMainBill() {
        when(billService.changeMailBill(anyString())).thenReturn(notMainBillDto);
        BillDto restoredBillDto =  billController.changeMailBill(BILL_NAME, USER_UUID);
        assertNotNull(restoredBillDto);
        assertTrue(restoredBillDto.getMainBill());

    }

    @Test
    void transferResources() {
//        TODO
    }

    @Test
    void deleteBill() {
        billService.deleteBill(BILL_NAME, USER_UUID);

        OperationStatuDto deleted = billController.deleteBill(BILL_NAME, USER_UUID);

        assertEquals(RequestOperationName.DELETE.name(), deleted.getOperationName());
        assertEquals(RequestOperationStatus.SUCCESS.name(), deleted.getOperationResult());
    }
}