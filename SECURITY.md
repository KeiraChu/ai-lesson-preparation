# Security Policy

## Supported version

Security fixes are applied to the latest commit on `main`.

## Reporting a vulnerability

Please do not open a public issue for a suspected vulnerability or exposed credential. Contact the repository owner through the GitHub profile instead. Include the affected component, reproduction steps, and possible impact. Do not include real student data, access tokens, passwords, or private teaching materials in the report.

## Public demo boundary

- The repository contains synthetic demo data and a local demo account only.
- `.env.example` contains placeholders. Real secrets must be stored outside Git and rotated if exposed.
- The default demo credentials and development fallbacks must not be used in production.
- Uploaded teaching materials may contain sensitive information. Production deployments require access control, retention rules, deletion support, malware scanning, and audit logs.
- The AI service is an internal service and must not be exposed directly to the public internet.
