FROM adoptopenjdk/openjdk16-openj9 as builder

WORKDIR /usr/app

COPY . .

RUN ./gradlew installDist -Dorg.gradle.daemon=false

FROM adoptopenjdk/openjdk16-openj9

WORKDIR /usr/app

COPY --from=builder /usr/app/build/install/spotify-api-to-file-stuff .

ENTRYPOINT ["/usr/app/bin/spotify-api-to-file-stuff"]