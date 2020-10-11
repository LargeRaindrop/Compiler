FROM gcc:10
WORKDIR /app/
COPY ./* ./
RUN gcc Lexer.cpp -o Lexer.out
RUN chmod +x Lexer.out