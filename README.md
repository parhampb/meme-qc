# Meme QC

This repo manages the quality required for the meme channel run by the pok team
If you want to join this channel, you will need to send a member of the pok team the following message along with a dank meme:

"The first rule of Meme Club is: you do not talk about Meme Club"

## Technical

Current infra setup is on GCP

The following services are being used:

- 3x Cloud Functions
- 3x Cloud Scheduler
- 2x Service Accounts

## Github Actions:
The following secrets need to be set in github for the actions to run correctly

- GCP_PROJECT_ID
- GCP_DEPLOY_SERVICE_ACCOUNT_KEY: The service account mentioned in the Service Accounts section
- SLACK_BOT_TOKEN: More info in the Cloud Functions section
- SLACK_SIGNING_SECRET: More info in the Cloud Functions section
- SLACK_DOMAIN: More info in the Cloud Functions section

### Cloud Functions:
All functions are deployed to GCP through GH actions using the gcloud cli - have a look at `.github/workflows/gcp-functions.yml`

All functions have the following runtime variables:

- SLACK_BOT_TOKEN - This is required and used to interact with Slack as a Bot
- SLACK_SIGNING_SECRET - Not currently used, but will be used later as a verification mechanism when more features are added to the app

Eviction Runner is one of the functions, it is responsible for evicting users from all channels the Bot is a part

Warning Runner is responsible for sending out warning messages to users that are about to be evicted

Statistics Runner is responsible for sending out the weekly statistics for the channel on how each member's messages performed

The runner requires the following runtime variables:

- SLACK_DOMAIN - This is the base domain used for slack in the following form `https://<ORG>.slack.com`, no trailing slash should be added

### Cloud Scheduler
The cloud schedulers are responsible for calling the warning runner, eviction runner and statistics runner functions

The schedulers are setup through click ops

The Warning Runner schedule is currently setup to run at 9AM AWST on Wednesdays and Thursdays every week

The Eviction Runner schedule is setup to run at 9AM AWST on Fridays

The Statistics Runner schedule is setup to run at 9:00:05AM AWST on Fridays - we want to make sure the eviction runner precedes this one

The Schedulers are configured to send a HTTP POST request to the following endpoints:

- https://australia-southeast1-<PROJECT_ID>.cloudfunctions.net/warning-runner
- https://australia-southeast1-<PROJECT_ID>.cloudfunctions.net/eviction-runner
- https://australia-southeast1-<PROJECT_ID>.cloudfunctions.net/statistics-runner

Structure: `https://<REGION>-<PROJECT_ID>.cloudfunctions.net/<FUNCTION_NAME>`

### Service Accounts
2 service accounts are required to limit access to the project and the execution of it

The service accounts are setup through click ops

Service account structure: `<SERVICE_ID>@<PROJECT_ID>.iam.gserviceaccount.com`

The first is `function-runner`, it is setup with a single role: `Cloud Functions Invoker`

This allows the scheduler to invoke the gcp function through an authenticated method

The second is `gh-builder`, it is setup with the following role: `roles/cloudfunctions.developer`

The following cli command needs to run after the above service account is created:

`gcloud iam service-accounts add-iam-policy-binding <PROJECT_ID>@appspot.gserviceaccount.com \
--member="serviceAccount:gh-builder@<PROJECT_ID>.iam.gserviceaccount.com" \
--role=roles/iam.serviceAccountUser`

This allows the GH Actions to deploy the functions through the gcloud cli