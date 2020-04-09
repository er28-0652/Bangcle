.class public Lcom/storm/fengyue/StubApplication;
.super Landroid/app/Application;
.source "StubApplication.java"


# static fields
.field public static TAG:Ljava/lang/String;

.field public static soName:Ljava/lang/String;


# direct methods
.method static constructor <clinit>()V
    .locals 1

    .line 16
    const-string v0, "fengyue"

    sput-object v0, Lcom/storm/fengyue/StubApplication;->TAG:Ljava/lang/String;

    .line 17
    const-string v0, "dexload"

    sput-object v0, Lcom/storm/fengyue/StubApplication;->soName:Ljava/lang/String;

    return-void
.end method

.method public constructor <init>()V
    .locals 0

    .line 15
    invoke-direct {p0}, Landroid/app/Application;-><init>()V

    return-void
.end method


# virtual methods
.method public attachBaseContext(Landroid/content/Context;)V
    .locals 4
    .param p1, "context"    # Landroid/content/Context;

    .line 21
    invoke-super {p0, p1}, Landroid/app/Application;->attachBaseContext(Landroid/content/Context;)V

    .line 22
    sget-object v0, Lcom/storm/fengyue/StubApplication;->TAG:Ljava/lang/String;

    const-string v1, "StubApplication.attachBaseContext"

    invoke-static {v0, v1}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 23
    sget-object v0, Landroid/os/Build;->CPU_ABI:Ljava/lang/String;

    .line 24
    .local v0, "abi":Ljava/lang/String;
    sget-object v1, Lcom/storm/fengyue/StubApplication;->TAG:Ljava/lang/String;

    new-instance v2, Ljava/lang/StringBuilder;

    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    const-string v3, "Build.CPU_ABI:"

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v2, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v2

    invoke-static {v1, v2}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 25
    const-string v1, "armeabi-v7a"

    invoke-virtual {v0, v1}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z

    move-result v1

    if-eqz v1, :cond_0

    .line 26
    sget-object v1, Lcom/storm/fengyue/StubApplication;->soName:Ljava/lang/String;

    invoke-static {v1}, Ljava/lang/System;->loadLibrary(Ljava/lang/String;)V

    .line 27
    invoke-static {p1}, Lcom/storm/fengyue/Native;->attachBaseContext(Landroid/content/Context;)V

    goto :goto_0

    .line 29
    :cond_0
    sget-object v1, Lcom/storm/fengyue/StubApplication;->TAG:Ljava/lang/String;

    new-instance v2, Ljava/lang/StringBuilder;

    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    const-string v3, "Bangcle is not supported abi:"

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v2, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v2

    invoke-static {v1, v2}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    .line 31
    :goto_0
    return-void
.end method

.method public onCreate()V
    .locals 2

    .line 34
    invoke-super {p0}, Landroid/app/Application;->onCreate()V

    .line 35
    sget-object v0, Lcom/storm/fengyue/StubApplication;->TAG:Ljava/lang/String;

    const-string v1, "StubApplication.onCreate"

    invoke-static {v0, v1}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 36
    invoke-static {p0}, Lcom/storm/fengyue/Native;->onCreate(Landroid/content/Context;)V

    .line 37
    return-void
.end method
