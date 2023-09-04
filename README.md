# Quranic Corpus API

Backend server API for the Quranic Arabic Corpus.

More details on the Corpus 2.0 project (and frontend code): https://github.com/kaisdukes/quranic-corpus

## Building Locally

This repo includes dependencies from [GitHub packages](https://github.com/kaisdukes/memseqdb/packages), which requires a classic personal access token:

```bash
export GITHUB_TOKEN=...
./gradlew build
```

## Logging

By default, the service logs to `/var/log/corpus/quranic-corpus-api.log`. Ensure that this folder exists and that the service has correct permissions to write to the folder.