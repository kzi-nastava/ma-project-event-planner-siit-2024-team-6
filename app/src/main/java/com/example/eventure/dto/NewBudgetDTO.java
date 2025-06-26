package com.example.eventure.dto;

import java.util.List;

public class NewBudgetDTO {
    List<BudgetItemDTO> budgetItems;

    public List<BudgetItemDTO> getBudgetItems() {
        return budgetItems;
    }

    public void setBudgetItems(List<BudgetItemDTO> budgetItems) {
        this.budgetItems = budgetItems;
    }
}
