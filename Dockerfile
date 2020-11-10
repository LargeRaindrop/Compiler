FROM gcc:10
WORKDIR /app/
COPY ./* ./
RUN gcc OPP.c -o OPP.out
RUN chmod +x OPP.out