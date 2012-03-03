// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.tynamo.ckeditor.integration.pages;

import javax.inject.Inject;

import org.apache.tapestry5.Link;
import org.apache.tapestry5.services.PageRenderLinkSource;

public class Index
{
	public static final String CKEDITOR_DEMO_CONTEXT = "some example text";

	@Inject
	private PageRenderLinkSource linkSource;

	public Link getCKEditorDemoNoContext()
	{
		return linkSource.createPageRenderLinkWithContext(CKEditorDemo.class);
	}
}
