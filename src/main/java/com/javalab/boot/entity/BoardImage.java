package com.javalab.boot.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.*;


@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "board")
public class BoardImage implements Comparable<BoardImage> {

    @Id
    private String uuid;

    private String fileName;

    private int ord;

    /*
      Board와 연관관계 매핑, 다대일 관계
      테이블로 치면 부모의 기본키가 자식인 이미지 테이블로 들어오게 된다.
      이 조건으로 실제 테이블에는 board_bno 외래키가 생성됨.
     */
    @ManyToOne
    private Board board;

    // 주로 정렬이 필요한 데이터 구조에서 활용, 이미지를 ord 컬럼 순으로 오름차순 정렬
    @Override
    public int compareTo(BoardImage other) {
        return this.ord - other.ord;    // 오름차순
    }

    public void changeBoard(Board board) {
        this.board = board;
    }

}
