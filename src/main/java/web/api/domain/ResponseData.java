package web.api.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL) // Null 값인 필드 제외
public class ResponseData {

    String resultCode;

    String resultMessage;

    Object resultData;

}
