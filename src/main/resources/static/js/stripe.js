const stripe = Stripe('pk_test_51RnonAFkQiq2POSqGoHl4QL9aXRGRu9akAPhlR1VfAwb9Jx8fCyGQAT2L5DKKnxm0oxVaupAIGPeQT5m7OzgjhnY007ieZvUew');
const paymentButton = document.querySelector('#paymentButton');

paymentButton.addEventListener('click', () => {
 stripe.redirectToCheckout({
   sessionId: sessionId
 })
});