# File Upload Demo

## For Frontend

Check out `./frontend/pages/index.tsx`, example uses axios and that is it

## For Backend

Check out `FileUploadController.kt`

## S3 Configuration(s)

1. Bucket must exist
2. Bucket uses default encryption SSE-KMS (server-side encryption using user created KMS key)
3. (for cross account access) bucket policy:

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "AddCannedAcl",
      "Effect": "Allow",
      "Principal": {
        "AWS": "<cross account arn>"
      },
      "Action": [
        "s3:PutObject",
        "s3:GetObject"
      ],
      "Resource": "arn:aws:s3:::cheerf.link/*"
    }
  ]
}
```

4. Accessor (IAM user, role running the back-end) must have access to KMS, example access policy:

```json
{
  "Version": "2012-10-17",
  "Id": "key-consolepolicy-3",
  "Statement": [
    {
      "Sid": "Allow access for another AWS account",
      "Effect": "Allow",
      "Principal": {
        "AWS": "<IAM role or account arn>"
      },
      "Action": "kms:*",
      "Resource": "*"
    }
  ]
}
```