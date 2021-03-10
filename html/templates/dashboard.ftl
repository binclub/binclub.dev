<#-- @ftlvariable name="user" type="dev.binclub.web.storage.User" -->
<#-- @ftlvariable name="licenses" type="java.util.List<dev.binclub.web.storage.License>" -->
<#-- @ftlvariable name="downloads" type="java.util.List<dev.binclub.web.storage.Download>" -->

<!DOCTYPE html>
<html lang="en">
<head>
    <!-- HTML Meta Tags -->
    <title>Binclub: Dashboard</title>
    <meta name="description" content="Binclub creates commercial obfuscators and reverse engineering tools">
    <meta name="keywords" content="binclub binscure bingait obfuscate obfuscator java deobfuscator disassembler jvm kotlin security drm decompilation bytecode">
    <meta content="text/html; charset=utf-8" http-equiv="content-type">
    <meta charset="UTF-8">
    <meta name="viewport" content="width=auto">

    <!-- Google / Search Engine Tags -->
    <meta itemprop="name" content="Binclub: Dashboard">
    <meta itemprop="description" content="Binclub creates commercial obfuscators and reverse engineering tools">
    <meta itemprop="image" content="http://binclub.dev/resources/icon.webp">

    <!-- Facebook Meta Tags -->
    <meta property="og:url" content="https://binclub.dev/binscure">
    <meta property="og:type" content="website">
    <meta property="og:title" content="Binclub: Dashboard">
    <meta property="og:description" content="Binclub creates commercial obfuscators and reverse engineering tools">
    <meta property="og:image" content="http://binclub.dev/resources/icon.webp">

    <!-- Twitter Meta Tags -->
    <meta name="twitter:card" content="summary">
    <meta name="twitter:title" content="Binclub: Dashboard">
    <meta name="twitter:description" content="Binclub creates commercial obfuscators and reverse engineering tools">
    <meta name="twitter:image" content="http://binclub.dev/resources/icon.webp">

    <link rel="canonical" href="https://binclub.dev/dashboard">
    <link href="/resources/icon.webp" rel="shortcut icon" type="image/x-icon">
    <link href="/style/shared.css" rel="stylesheet" type="text/css">
    <link href="/style/dashboard.css" rel="stylesheet" type="text/css">
</head>
<body>
<div id="main">
    <div id="header">
        <nav>
            <picture>
                <source srcset="/resources/Binclub_2_75.webp" type="image/webp">
                <source srcset="/resources/Binclub_2_75.png" type="image/png">
                <img src="/resources/Binclub_2_75.png" alt="padlock" onclick="goto('/')" style="cursor: pointer">
            </picture>
            <ul>
                <li><a href="/binscure">Binscure</a></li>
                <li><a href="/bingait">Bingait</a></li>
                <li><a href="https://blog.binclub.dev" target="_blank">Blog</a></li>
                <li><a href="/discord" target="_blank">Discord</a></li>
                <li><a href="/logout">Logout</a></li>
            </ul>
        </nav>
    </div>
    <!--email_off-->
    <div id="content">
        <h3>Hello ${ user.email }, you own ${ licenses?size } licenses</h3>
        <div id="purchases">
            <#if licenses?size gt 0 || downloads?size gt 0 >
                <h4>Your Licenses</h4>
                <table>
                    <tbody>
                    <tr>
                        <th>Product</th>
                        <th>Purchased</th>
                    </tr>
                    <#list licenses as license>
                        <tr>
                            <td>${ license.product.name }</td>
                            <td>${ formatDate(license.datePurchased) }</td>
                        </tr>
                    </#list>
                    </tbody>
                </table>

                <h4>Your Downloads</h4>
                <table>
                    <tbody>
                    <tr>
                        <th>Name</th>
                        <th>Description</th>
                        <th>Date Released</th>
                        <th>Download</th>
                    </tr>
                    <#list downloads as download>
                        <tr>
                            <td>${ download.product.name }.${ download.name }</td>
                            <td>${ formatText(download.description) }</td>
                            <td>${ formatDate(download.dateReleased) }</td>
                            <td>
                                <button onclick="download(${download.id})">Download</button>
                            </td>
                        </tr>
                    </#list>
                    </tbody>
                </table>
            </#if>
        </div>
    </div>
    <!--/email_off-->
</div>
<script src="/script/shared.js"></script>
<script src="/script/dashboard.js"></script>
<link href="https://fonts.googleapis.com/css?family=Lato" rel="stylesheet">
</body>
</html>
