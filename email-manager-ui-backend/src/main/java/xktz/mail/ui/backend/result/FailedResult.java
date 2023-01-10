package xktz.mail.ui.backend.result;

/**
 * Result failing request
 *
 * @author XKTZ
 * @date 2022-06-27
 */
public class FailedResult implements Result<String> {

    private String msg;

    /**
     * @param msg message of failing
     */
    public FailedResult(String msg) {
        this.msg = msg;
    }

    @Override
    public ResultStatus getStatus() {
        return ResultStatus.FAILED;
    }

    @Override
    public String getContent() {
        return msg;
    }
}
