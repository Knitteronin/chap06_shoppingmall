package com.javalab.boot.entity;


import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Base Entity
 * - 생성일, 수정일을 관리하는 추상 클래스로 모든 엔티티의 상위 클래스로 사용
 * - @MappedSuperclass: 테이블로 매핑하지 않고 자식 클래스에 엔티티의 매핑 정보를 상속하기 위한 어노테이션
 * - @EntityListeners: 엔티티의 생명주기를 감지하는 리스너를 설정하는 어노테이션,
 *   엔티티가 생성되면 생성일, 수정일을 자동으로 업데이트
 * - @CreatedDate: 엔티티가 생성되어 저장될 때 시간이 자동 저장되는 어노테이션
 * - @LastModifiedDate: 엔티티가 수정되어 저장될 때 시간이 자동 저장되는 어노테이션
 * - 메인 클래스에 @EnableJpaAuditing 어노테이션을 추가해야 동작(중요)
 */
@MappedSuperclass
@EntityListeners(value = { AuditingEntityListener.class })
@Getter
abstract class BaseEntity {

    @CreatedDate
    @Column(name = "regdate", updatable = false)
    private LocalDateTime regDate;

    @LastModifiedDate
    @Column(name ="moddate" )
    private LocalDateTime modDate;

}
