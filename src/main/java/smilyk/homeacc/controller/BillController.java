package smilyk.homeacc.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import smilyk.homeacc.dto.BillDto;
import smilyk.homeacc.dto.OperationStatuDto;
import smilyk.homeacc.dto.TransferResourcesBetweenBillsDto;
import smilyk.homeacc.dto.TransferResourcesResponseDto;
import smilyk.homeacc.enums.RequestOperationName;
import smilyk.homeacc.enums.RequestOperationStatus;
import smilyk.homeacc.service.bill.BillService;
import smilyk.homeacc.service.validation.ValidatorService;

import java.util.List;

@RestController
@Api( value = "home-accounting" , description = "Operations pertaining to bills in HomeAccounting" )
@RequestMapping("v1/bill")
public class BillController {

    @Autowired
    BillService billService;

    @Autowired
    ValidatorService validatorService;

    @ApiOperation(value = "Create bill", response = BillDto.class)
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public BillDto createBill(@Validated @RequestBody BillDto billDto) {

        validatorService.checkUniqueBill(billDto.getBillName());
        validatorService.checkMainBill(billDto.getMainBill());
        if (billDto.getSumUkr() == null) {
            billDto.setSumUkr(0.0);
        }
        if (billDto.getSumIsr() == null) {
            billDto.setSumIsr(0.0);
        }
        if (billDto.getSumUsa() == null) {
            billDto.setSumUsa(0.0);
        }
        if (billDto.getDescription() == null) {
            billDto.setDescription("");
        }
        if (billDto.getMainBill() == null) {
            billDto.setMainBill(false);
        }
        return billService.createBill(billDto);
    }

    /**
     * getting bill by bills name
     *
     * @param billName
     * @return BillDto
     */

    @ApiOperation(value = "Search a bill by bills name and user Uuid", response = BillDto.class)
    @GetMapping("/{billName}/{userUuid}")
    public BillDto getBillByBillName(@PathVariable String billName, @PathVariable String userUuid) {
        return billService.getBillByBillName(billName, userUuid);
    }


    @ApiOperation(value = "View a list of all bills by user")
    @GetMapping("/allBills/{userUuid}")
    public List<BillDto> getAllBillsByUserUuid(@PathVariable String userUuid) {
        return billService.getAllBillsByUser(userUuid);
    }

    /**
     * @param userUuid
     * @param billsCurrency
     * @return List<BillDto>
     */
    @ApiOperation(value = "View a list of all bills by user and currency")
    @GetMapping("/allBills/{userUuid}/{billsCurrency}")
    public List<BillDto> getAllBillsByUserUuidAndCurrency(@PathVariable String userUuid,
                                                          @PathVariable String billsCurrency) {
        validatorService.checkCurrencyNameValid(billsCurrency);
        validatorService.checkUserExists(userUuid);
        return billService.getAllBillsByUserUuidAndCurrency(userUuid, billsCurrency);
    }

//    TODO check method of main bill
    /**
     * @param billName
     * @return BillDto
     */
//    checked
    @ApiOperation(value = "Change main bill by user", response = BillDto.class)
    @PutMapping("/{billName}/{userUuid}")
    public BillDto changeMailBill(@PathVariable String billName, @PathVariable String userUuid) {
        validatorService.checkBill(billName);
        validatorService.checkBillByUser(billName, userUuid);
        return billService.changeMailBill(billName);
    }

    @ApiOperation(value = "Transfer money from bill to another bill ",
        response = TransferResourcesResponseDto.class)
    @PutMapping()
    public TransferResourcesResponseDto transferResources(@Validated @RequestBody TransferResourcesBetweenBillsDto transferDto) {
//            validatorService.checkBillByUserAndCurrency(transferDto.getBillNameFrom(), transferDto.getUserUuid(),
//                    transferDto.getCurrency());
//            validatorService.checkBillByUserAndCurrency(transferDto.getBillNameTo(), transferDto.getUserUuid(),
//                    transferDto.getCurrency());
//        }
        validatorService.checkBillByUser(transferDto.getBillNameFrom(), transferDto.getUserUuid());
        return billService.transferResources(transferDto);
    }

    /**
     * @param billName
     * @param userUuid
     * @return SUCCESS or ERROR
     */
    //checked
    //    front +
    @ApiOperation(value = "Delete a bill")
    @DeleteMapping("/{billName}/{userUuid}")
    public OperationStatuDto deleteBill(@PathVariable String billName, @PathVariable String userUuid) {
        validatorService.checkBillByUser(billName, userUuid);
        validatorService.checkMainBillsForDeleted(billName);
        OperationStatuDto returnValue = new OperationStatuDto();
        returnValue.setOperationName(RequestOperationName.DELETE.name());
        billService.deleteBill(billName, userUuid);
        returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
        return returnValue;
    }

    //    TODO create test
    /**
     * method that we need for validation create bill from front
     * @param billName
     * @param userUuid
     * return True or False
     */
//    front +
    @ApiOperation(value = "Search a user with an email")
    @GetMapping("/valid/{billName}/{userUuid}")
    public Boolean getUserByUserEmail(@PathVariable String billName, @PathVariable String userUuid){
        return billService.getBillByNameForValidation(billName, userUuid);
    }

}
