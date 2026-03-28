package com.salary.admin.enums;

import lombok.Getter;

@Getter
public enum SalaryItemCategoryEnum {
    INCOME(1, "salary_income_type"),
    DEDUCTION(2, "salary_deduction_type"),
    TAX_SOCIAL(3, "salary_tax_social_type"),
    COMPANY_EXPENSE(4, "salary_company_expense_type"),
    UNKNOWN(0, "common_dict");

    private final int code;
    private final String dictType;

    SalaryItemCategoryEnum(int code, String dictType) {
        this.code = code;
        this.dictType = dictType;
    }

    public static SalaryItemCategoryEnum fromCode(Integer code) {
        if (code == null) return UNKNOWN;
        for (SalaryItemCategoryEnum c : values()) {
            if (c.code == code) return c;
        }
        return UNKNOWN;
    }
}

