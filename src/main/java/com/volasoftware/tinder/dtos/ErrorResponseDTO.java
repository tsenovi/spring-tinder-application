package com.volasoftware.tinder.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
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
@ApiModel(description = "Error response information")
public class ErrorResponseDTO {

  @ApiModelProperty(value = "Http status code", example = "404")
  private HttpStatus status;

  @ApiModelProperty(value = "Error message", example = "Not Found")
  private String message;

  @ApiModelProperty(value = "Timestamp of the error", example = "2021-15-08 14:32:17")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
  private LocalDateTime timestamp;

}
