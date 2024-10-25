import { ClassicEditor, Bold, Italic, Underline, Essentials, Paragraph } from 'ckeditor5';

const editor = await ClassicEditor.create(document.querySelector('#editor'), {
	plugins: [Essentials, Paragraph, Bold, Italic, Underline],
	toolbar: {
		items: ['bold', 'italic', 'underline']
	}
}).catch(error => {
	console.log(error);
});

editor.execute('bold', 'italic', 'underline');