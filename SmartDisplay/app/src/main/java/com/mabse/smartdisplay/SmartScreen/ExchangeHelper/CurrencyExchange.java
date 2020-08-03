package com.mabse.smartdisplay.SmartScreen.ExchangeHelper;

public class CurrencyExchange {
    private String date;

    private Rates rates;

    private String base;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Rates getRates() {
        return rates;
    }

    public void setRates(Rates rates) {
        this.rates = rates;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    @Override
    public String toString() {
        return "ClassPojo [date = " + date + ", rates = " + rates + ", base = " + base + "]";
    }

    public class Rates {
        private String CHF;
        private String EUR;
        private String GBP;
        private String CAD;
        private String USD;
        private String SEK;
        private String TRY;

        private String PLN;
        private String KRW;
        private String ILS;
        private String AUD;
        private String SGD;
        private String MXN;
        private String ZAR;
        private String NZD;
        private String NOK;
        private String CNY;
        private String BGN;
        private String MYR;
        private String RUB;
        private String CZK;
        private String JPY;




        public String getCHF() {
            return CHF;
        }

        public void setCHF(String CHF) {
            this.CHF = CHF;
        }

        public String getEUR() {
            return EUR;
        }

        public void setEUR(String EUR) {
            this.EUR = EUR;
        }

        public String getGBP() {
            return GBP;
        }

        public void setGBP(String GBP) {
            this.GBP = GBP;
        }

        public String getCAD() {
            return CAD;
        }

        public void setCAD(String CAD) {
            this.CAD = CAD;
        }

        public String getUSD() {
            return USD;
        }

        public void setUSD(String USD) {
            this.USD = USD;
        }

        public String getTRY() {
            return TRY;
        }

        public void setTRY(String TRY) {
            this.TRY = TRY;
        }

        public String getSEK() {
            return SEK;
        }

        public void setSEK(String SEK) {
            this.SEK = SEK;
        }

        public String getPLN() {
            return PLN;
        }

        public void setPLN(String PLN) {
            this.PLN = PLN;
        }

        public String getKRW() {
            return KRW;
        }

        public void setKRW(String KRW) {
            this.KRW = KRW;
        }

        public String getILS() {
            return ILS;
        }

        public void setILS(String ILS) {
            this.ILS = ILS;
        }

        public String getAUD() {
            return AUD;
        }

        public void setAUD(String AUD) {
            this.AUD = AUD;
        }

        public String getSGD() {
            return SGD;
        }

        public void setSGD(String SGD) {
            this.SGD = SGD;
        }

        public String getMXN() {
            return MXN;
        }

        public void setMXN(String MXN) {
            this.MXN = MXN;
        }

        public String getZAR() {
            return ZAR;
        }

        public void setZAR(String ZAR) {
            this.ZAR = ZAR;
        }

        public String getNZD() {
            return NZD;
        }

        public void setNZD(String NZD) {
            this.NZD = NZD;
        }

        public String getNOK() {
            return NOK;
        }

        public void setNOK(String NOK) {
            this.NOK = NOK;
        }

        public String getCNY() {
            return CNY;
        }

        public void setCNY(String CNY) {
            this.CNY = CNY;
        }

        public String getBGN() {
            return BGN;
        }

        public void setBGN(String BGN) {
            this.BGN = BGN;
        }

        public String getMYR() {
            return MYR;
        }

        public void setMYR(String MYR) {
            this.MYR = MYR;
        }

        public String getRUB() {
            return RUB;
        }

        public void setRUB(String RUB) {
            this.RUB = RUB;
        }

        public String getCZK() {
            return CZK;
        }

        public void setCZK(String CZK) {
            this.CZK = CZK;
        }

        public String getJPY() {
            return JPY;
        }

        public void setJPY(String JPY) {
            this.JPY = JPY;
        }
    }
}