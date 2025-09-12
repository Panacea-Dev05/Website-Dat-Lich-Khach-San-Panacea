package panacea.website_dat_lich_khach_san.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import panacea.website_dat_lich_khach_san.entity.InventoryTransaction;
import java.util.List;

@Repository
public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Integer> {
    
    @Query("SELECT t FROM InventoryTransaction t LEFT JOIN FETCH t.inventoryItem")
    List<InventoryTransaction> findAllWithInventoryItem();
}
