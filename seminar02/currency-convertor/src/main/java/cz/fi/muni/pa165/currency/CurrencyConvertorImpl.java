package cz.fi.muni.pa165.currency;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;


/**
 * This is base implementation of {@link CurrencyConvertor}.
 *
 * @author petr.adamek@embedit.cz
 */
public class CurrencyConvertorImpl implements CurrencyConvertor {

    private final ExchangeRateTable exchangeRateTable;
    //private final Logger logger = LoggerFactory.getLogger(CurrencyConvertorImpl.class);

    public CurrencyConvertorImpl(ExchangeRateTable exchangeRateTable) {
        this.exchangeRateTable = exchangeRateTable;
    }

    @Override
    public BigDecimal convert(Currency sourceCurrency, Currency targetCurrency, BigDecimal sourceAmount) {
        if (targetCurrency == null) {
            throw new IllegalArgumentException("Target currency is null.");
        }
        if (sourceAmount == null) {
            throw new IllegalArgumentException("Source amount is null.");
        }
        if (sourceCurrency == null) {
            throw new IllegalArgumentException("Source currency is null.");
        }
        
        try {
            BigDecimal rate = exchangeRateTable.getExchangeRate(sourceCurrency, targetCurrency);
            
            if (rate == null) {
                throw new UnknownExchangeRateException("Exchange rate is null.");
            }
            return rate.multiply(sourceAmount).setScale(2, RoundingMode.HALF_EVEN);
        } catch (ExternalServiceFailureException ex) {
            throw new UnknownExchangeRateException("Error when fetching exchange rate", ex);
        }
    }

}
