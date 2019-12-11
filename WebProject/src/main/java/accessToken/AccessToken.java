package accessToken;

public class AccessToken {
	private String tokenName; //获取到的凭证
    private int expireSecond;    //凭证有效时间  单位:秒

    public String getTokenName() {
        return tokenName;
    }

    public void setTokenName(String tokenName) {
        this.tokenName = tokenName;
    }

    public int getExpireSecond() {
        return expireSecond;
    }

    public void setExpireSecond(int expireSecond) {
        this.expireSecond = expireSecond;
    }
}
