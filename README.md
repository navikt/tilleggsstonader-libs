# tilleggsstonader-libs

Fellesbiblioteker for tilleggsstønader, med inspirasjon fra https://github.com/navikt/familie-felles

## HTTP-klienter

Følgende RestTemplate-bønner er tilgjengelige:
- `utenAuth` - uten autentisering
- `tokenExchange` - med token exchange
- `azure` - med Azure JWT bearer token
- `azureClientCredential` - med Azure client credentials
- `azureOnBehalfOf` - med Azure on-behalf-of flow

Default timeouts er 2s (connect) og 25s (read). Har du behov for å endre timeout, kan du sette følgende verdi i `application.yaml`: 
```
tilleggsstonader:
  http-client:
    connect-timeout: PT3S    # 3 sekunder
    read-timeout: PT30S      # 30 sekunder
```