package cz.markovda.friendsnet.post.dos;

import cz.markovda.friendsnet.auth.dos.impl.UserDO;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 07.01.22
 */
@ToString
@Getter
@Entity(name = PostDO.TABLE_NAME)
public class PostDO {

    public static final String TABLE_NAME = "post";
    public static final String ID = "id";
    public static final String AUTHOR_ID = "author_id";
    public static final String TITLE = "title";
    public static final String CONTENT = "content";
    public static final String DATE_CREATED = "date_created";
    public static final String ANNOUNCEMENT = "announcement";

    public static final String SEQUENCE_NAME = "post_id_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
    @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = 1)
    private Integer id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = AUTHOR_ID, nullable = false)
    @ToString.Exclude
    private UserDO author;

    @Column(name = TITLE, nullable = false)
    private String title;

    @Lob
    @Column(name = CONTENT, nullable = false)
    private String content;

    @Column(name = DATE_CREATED, nullable = false)
    private LocalDateTime dateCreated;

    @Column(name = ANNOUNCEMENT, nullable = false)
    private Boolean announcement;

    public PostDO() {
    }

    public PostDO(final UserDO author, final String title, final String content) {
        this(author, title, content, false, LocalDateTime.now());
    }
    public PostDO(final UserDO author, final String title, final String content, final boolean announcement, final LocalDateTime dateCreated) {
        this.author = author;
        this.title = title;
        this.content = content;
        this.announcement = announcement;
        this.dateCreated = dateCreated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        PostDO postDO = (PostDO) o;
        return id != null && Objects.equals(id, postDO.id) &&
                Objects.equals(author, postDO.author) &&
                Objects.equals(title, postDO.title) &&
                Objects.equals(content, postDO.content) &&
                Objects.equals(dateCreated, postDO.dateCreated);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
