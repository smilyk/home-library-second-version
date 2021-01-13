package smilyk.homeacc.repo;

import org.springframework.data.repository.PagingAndSortingRepository;
import smilyk.homeacc.model.Bill;

import java.util.Optional;

public interface BillRepository extends PagingAndSortingRepository<Bill, Long> {

    Optional<Bill> findByBillName(String billName);

    Optional<Bill> findByBillNameAndDeleted(String billName, boolean b);

    Optional<Bill> findByMainBill(boolean b);
}