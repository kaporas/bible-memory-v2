package com.bible.scoring.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ScoringRequest {

    @NotBlank(message = "참가자 이름을 입력해주세요")
    @Size(min = 2, max = 20, message = "이름은 2~20자 사이로 입력해주세요")
    @Pattern(regexp = "^[가-힣a-zA-Z\\s]+$", message = "이름은 한글 또는 영문만 입력해주세요")
    private String participantName;

    @NotBlank(message = "대회 이름을 입력해주세요")
    private String contestName;

    private String bibleBook;

    private String bibleChapter;

    private List<VerseInputDto> verses;
}
