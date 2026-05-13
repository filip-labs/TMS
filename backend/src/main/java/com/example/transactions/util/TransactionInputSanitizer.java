package com.example.transactions.util;

import org.springframework.web.util.HtmlUtils;

public final class TransactionInputSanitizer {

    private TransactionInputSanitizer() {
    }

    public static String trim(String value) {
        return value == null ? null : value.strip();
    }

    public static String escapeHtml(String value) {
        return value == null ? null : HtmlUtils.htmlEscape(value);
    }
}