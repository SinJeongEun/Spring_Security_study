package com.sp.fc.paper.domain;

import com.sp.fc.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="sp_paper_template")
public class PaperTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paperTemplateId;

    private String name;

    private Long userId;

    @Transient // 테이블의 컬럼과 매핑하지 않는다, 영속성 컨텍스트에서 제외한다
    private User creator;

    private int total;

    //시험지
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(foreignKey = @ForeignKey(name="paperTemplateId"))
    private List<Problem> problemList;

    private long publishedCount;

    private long completeCount;

    @Column(updatable = false)
    private LocalDateTime created;

    private LocalDateTime updated;

}
