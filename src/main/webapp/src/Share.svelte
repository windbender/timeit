<script>
        import QrCode from "svelte-qrcode"
		import { guid  } from './guidstore.js';
		import { api_url } from "./util.js";

		var eventURL=api_url+'?guid='+$guid;
</script>

<main>

<div class="container">
  <QrCode value="{eventURL}" />
</div>

</main>

<style>
        table {
          margin-left: auto;
          margin-right: auto;
        }
	main {
		text-align: center;
		padding: 1em;
		margin: 0 auto;
	}

	h1 {
		color: #ff3e00;
		text-transform: uppercase;
		font-size: 3em;
		font-weight: 300;
	}

	@media (min-width: 640px) {
		main {
			max-width: none;
		}
	}
</style>
