package com.volasoftware.tinder.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(description = "Response information")
public class ResponseDTO<T> {

  @ApiModelProperty(value = "Http status code", example = "200")
  private HttpStatus status;

  @ApiModelProperty(value = "Message", example = "OK")
  private String message;

  @ApiModelProperty(value = "Timestamp of the error", example = "2021-15-08 14:32:17")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
  private LocalDateTime timestamp;

  @ApiModelProperty(value = "Additional data", example = "Object or collection of objects")
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private T data;

}
