package dns;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * https://www.rfc-editor.org/rfc/rfc1035#section-4.1
 */
public final class DnsMessage implements DnsRecord {

    private final DnsHeader header;
    private final List<DnsQuestion> questions;
    private final List<DnsAnswer> answers;

    public DnsMessage(DnsMessage.Builder builder) {
        Objects.requireNonNull(builder.header, "Header must not be null.");

        this.header = builder.header;
        this.questions = List.copyOf(builder.questions);
        this.answers = List.copyOf(builder.answers);
    }

    public DnsHeader getHeader() {
        return header;
    }

    public List<DnsQuestion> getQuestions() {
        return questions;
    }

    public List<DnsAnswer> getAnswers() {
        return answers;
    }

    @Override
    public int getSize() {
        return header.getSize() +
                questions.stream().mapToInt(DnsQuestion::getSize).sum() +
                answers.stream().mapToInt(DnsAnswer::getSize).sum();
    }

    public static Builder builder() {
        return new DnsMessage.Builder();
    }

    public static class Builder {

        private DnsHeader header;
        private List<DnsQuestion> questions = new ArrayList<>();
        private List<DnsAnswer> answers = new ArrayList<>();

        public Builder withHeader(DnsHeader header) {
            this.header = header;
            return this;
        }

        public DnsHeader getHeader() {
            return header;
        }

        public Builder addQuestion(DnsQuestion question) {
            this.questions.add(question);
            return this;
        }

        public Builder withQuestions(DnsQuestion... questions) {
            this.questions = List.of(questions);
            return this;
        }

        public List<DnsQuestion> getQuestions() {
            return questions;
        }

        public Builder addAnswer(DnsAnswer answer) {
            this.answers.add(answer);
            return this;
        }

        public Builder withAnswers(DnsAnswer... answers) {
            this.answers = List.of(answers);
            return this;
        }

        public List<DnsAnswer> getAnswers() {
            return answers;
        }

        public DnsMessage build() {
            return new DnsMessage(this);
        }

    }

}
