# User
* id: String
* created: DateTime
* updated: DateTime
* name: String
* email: String?
* banned: Boolean
* verified: Boolean
* logins: UserLogin[]
* worlds: World[]

# UserLogin
* type: Enum (Local, Twitter, Google, Facebook, Reddit)
* externalId: String

# World
* id: String
* created: DateTime
* updated: DateTime
* name: String
* owner: User