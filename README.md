# binclub.dev

Hosted at [binclub.dev](https://binclub.dev) (who guessed)

Site used to advertise Binclub products as well as providing user and license management services.

Much bad code due to lack of Kotlin and Ktor experience at the time of design.
If I ever rewrite this it will likely be in Rust with actix.

Uses:
* Ktor with a netty backend for serving most components.
* Apache Freemarker for dynamic webpages
* Server side google analytics
    - No tracking requests/scripts client side (except recaptcha)
    - Your IP is anonymised from google's servers
    - You are tracked by a UUID cookie, but your activity on binclub.dev will not be linkable to you or any activity on any other website
* BlowCrypt password hashing
* Hikari to connect to mysql database
* Google recaptcha for login/register forms (plan to move to hcaptcha one day)

The site also provides a proxy to access a WriteFreely instance hosted on the same machine.
Nginx should really be used for this.

The code has many hardcoded urls and if you want to rehost on another domain it will not be easy.
This is somewhat purposeful.
