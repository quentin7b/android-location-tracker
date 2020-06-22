package fr.quentinklein.slt

class ProviderError(var provider: String, detailMessage: String?) : Throwable(detailMessage) {

    override fun toString() = super.toString() + " | ProviderError { provider='$provider' }"

}