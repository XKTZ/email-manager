package xktz.mail.ui.backend.result;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Result of a request
 *
 * @param <T> type of result return
 * @author XKTZ
 * @date 2022-06-27
 */
public interface Result<T> extends Serializable {

    Result<Void> EMPTY_SUCCESS_RESULT = new Result<Void>() {
        @Override
        public ResultStatus getStatus() {
            return ResultStatus.SUCCESS;
        }

        @Override
        public Void getContent() {
            return null;
        }
    };

    /**
     * Status of result
     * @return status
     */
    @JsonGetter("status")
    ResultStatus getStatus();

    /**
     * Content of result
     * @return data
     */
    @JsonGetter("content")
    T getContent();

    /**
     * Return a success result with data
     * @param data data
     * @param <E> data type
     * @return success result
     */
    static <E> Result<E> successResultOf(E data) {
        return new Result<>() {
            @Override
            public ResultStatus getStatus() {
                return ResultStatus.SUCCESS;
            }

            @Override
            public E getContent() {
                return data;
            }
        };
    }
}
